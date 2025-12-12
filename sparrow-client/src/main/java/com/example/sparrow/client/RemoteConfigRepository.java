package com.example.sparrow.client;

import com.example.sparrow.client.acl.ServiceWrapper;
import com.example.sparrow.client.acl.SparrowConfiguration;
import com.example.sparrow.client.infrastructure.SparrowInjector;
import com.example.sparrow.client.model.ConfigChange;
import com.example.sparrow.client.model.ConfigChangeEvent;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.sparrow.client.constant.SparrowConstant.SPARROW_CONFIG_SERVICE_SERVICE_ID;
import static com.example.sparrow.client.constant.SparrowConstant.SPARROW_DISCOVERY_URL_PROPERTY_KEY;

@Slf4j
public class RemoteConfigRepository {
    private final ExecutorService longPollingService;
    private final ExecutorService syncService;
    private final AtomicBoolean longPollStarted;
    private final AtomicLong releaseId;
    private final AtomicReference<SparrowConfiguration> configCache;
    private final RestTemplate restTemplate;
    private final List<ConfigChangeListener> listeners;
    private final String namespace;

    public RemoteConfigRepository(String namespace) {
        this.longPollingService = Executors.newSingleThreadExecutor();
        this.syncService = Executors.newSingleThreadExecutor();
        this.longPollStarted = new AtomicBoolean(false);
        this.releaseId = new AtomicLong(-1L);
        this.configCache = new AtomicReference<>();
        this.restTemplate = SparrowInjector.getInstance(RestTemplate.class);
        this.listeners = new CopyOnWriteArrayList<>();
        this.namespace = namespace;
        initialize();
    }

    public void addListener(ConfigChangeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public String[] getPropertyNames() {
        Set<String> propertyNames = Optional.ofNullable(configCache.get()).map(SparrowConfiguration::getConfiguration).map(Properties::stringPropertyNames).orElse(Collections.emptySet());
        return StringUtils.toStringArray(propertyNames);
    }

    public Object getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null && configCache.get() != null) {
            value = configCache.get().getConfiguration().getProperty(key);
        }
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) {
            log.warn("Could not load config {} from Sparrow, will return default value {} instead", key, defaultValue);
        }
        return value == null ? defaultValue : value;
    }

    private void initialize() {
        String discoveryUrl = System.getProperty(SPARROW_DISCOVERY_URL_PROPERTY_KEY);
        if (!StringUtils.hasLength(namespace) || !StringUtils.hasLength(discoveryUrl)) {
            longPollStarted.set(false);
            throw new RuntimeException("Sparrow client config does not set correctly");
        }
        if (!discoveryUrl.endsWith("/")) {
            discoveryUrl += "/";
        }
        sync(namespace, discoveryUrl);
        longPoll(namespace, discoveryUrl);
    }

    private synchronized void sync(String namespaceName, String discoveryUrl) {
        try {
            String serverUrl = getServiceWrapper(discoveryUrl).getUrl();
            String requestUrl = serverUrl + String.format("client/config/namespaceName/%s", namespaceName);
            ResponseEntity<SparrowConfiguration> responseEntity = restTemplate.getForEntity(requestUrl, SparrowConfiguration.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.hasBody()) {
                Properties oldProperties = configCache.get() == null ? null : configCache.get().getConfiguration();
                List<ConfigChange> configChanges = calcConfigChange(oldProperties, responseEntity.getBody().getConfiguration());
                configCache.set(responseEntity.getBody());
                for (ConfigChangeListener listener : listeners) {
                    listener.onChange(new ConfigChangeEvent(configChanges));
                }
            }
        } catch (Throwable ex) {
            // for example 404
            log.error("Something went wrong when sync configs from remote", ex);
        }
    }

    private void longPoll(String namespaceName, String discoveryUrl) {
        if (!longPollStarted.compareAndSet(false, true)) {
            return;
        }
        longPollingService.submit(() -> {
            doLongPoll(namespaceName, discoveryUrl);
            try {
                TimeUnit.MILLISECONDS.sleep(2 * 1000L);
            } catch (InterruptedException ignored) {
            }
        });
    }

    private void doLongPoll(String namespaceName, String discoveryUrl) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String serverUrl = getServiceWrapper(discoveryUrl).getUrl();
                String requestUrl = serverUrl + String.format("client/notification/namespaceName/%s/notificationId/%d", namespaceName, releaseId.get());
                ResponseEntity<Long> responseEntity = restTemplate.getForEntity(requestUrl, Long.class);
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.hasBody()) {
                    releaseId.set(responseEntity.getBody());
                    syncService.submit(() -> {
                        sync(namespaceName, discoveryUrl);
                    });
                }
            } catch(Throwable ex) {
                // Can not stop
                log.error("Something went wrong when long polling", ex);
                try {
                    TimeUnit.MILLISECONDS.sleep(2 * 1000L);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private ServiceWrapper getServiceWrapper(String discoveryUrl) {
        String requestUrl = discoveryUrl + String.format("discovery/%s/loadBalance", SPARROW_CONFIG_SERVICE_SERVICE_ID);
        ResponseEntity<ServiceWrapper> responseEntity = restTemplate.getForEntity(requestUrl, ServiceWrapper.class);
        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.hasBody()) {
            return responseEntity.getBody();
        }
        throw new RuntimeException("Something went wrong when getting service wrapper");
    }

    private List<ConfigChange> calcConfigChange(Properties oldProperties, Properties newProperties) {
        if (oldProperties == null) {
            oldProperties = new Properties();
        }
        if (newProperties == null) {
            newProperties = new Properties();
        }
        Set<String> previousKeys = oldProperties.stringPropertyNames();
        Set<String> currentKeys = newProperties.stringPropertyNames();

        Set<String> commonKeys = Sets.intersection(previousKeys, currentKeys);
        Set<String> newKeys = Sets.difference(currentKeys, commonKeys);
        Set<String> removedKeys = Sets.difference(previousKeys, commonKeys);

        List<ConfigChange> configChanges = new ArrayList<>();
        for (String newKey : newKeys) {
            configChanges.add(new ConfigChange(newKey, null, newProperties.getProperty(newKey),
                    ConfigChange.ConfigChangeType.CREATE));
        }

        for (String removedKey : removedKeys) {
            configChanges.add(new ConfigChange(removedKey, oldProperties.getProperty(removedKey), null,
                    ConfigChange.ConfigChangeType.DELETE));
        }

        for (String commonKey : commonKeys) {
            String previousValue = oldProperties.getProperty(commonKey);
            String currentValue = newProperties.getProperty(commonKey);
            if (Objects.equals(previousValue, currentValue)) {
                continue;
            }
            configChanges.add(new ConfigChange(commonKey, previousValue, currentValue,
                    ConfigChange.ConfigChangeType.MODIFY));
        }
        return configChanges;
    }
}
