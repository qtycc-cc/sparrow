package com.example.sparrow.client.spring.processor;

import com.example.sparrow.client.ConfigChangeListener;
import com.example.sparrow.client.ConfigPropertySource;
import com.example.sparrow.client.factory.ConfigPropertySourceFactory;
import com.example.sparrow.client.infrastructure.SparrowInjector;
import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Set;

public class SparrowPropertySourceProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
    private ConfigurableEnvironment environment;
    private static final Set<BeanFactory> AUTO_UPDATE_BEAN_FACTORIES = Sets.newConcurrentHashSet();
    private final ConfigPropertySourceFactory configPropertySourceFactory = SparrowInjector.getInstance(ConfigPropertySourceFactory.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        initPropertySource();
        initAutoUpdate(beanFactory);
    }

    private void initPropertySource() {
        if (environment.getPropertySources().contains("sparrowPropertySource")) {
            return;
        }
        String[] namespaceNames = System.getProperty("sparrow.namespaceNames").split(",");
        CompositePropertySource composite = new CompositePropertySource("sparrowPropertySource");
        for (String namespace : namespaceNames) {
            composite.addPropertySource(configPropertySourceFactory.get(namespace));
        }
        environment.getPropertySources().addFirst(composite);
    }

    private void initAutoUpdate(ConfigurableListableBeanFactory  beanFactory) {
        if (!AUTO_UPDATE_BEAN_FACTORIES.add(beanFactory)) {
            return;
        }
        ConfigChangeListener configChangeListener = new ConfigChangeListener(beanFactory);
        List<ConfigPropertySource> configPropertySources = configPropertySourceFactory.getAll();
        for (ConfigPropertySource configPropertySource : configPropertySources) {
            configPropertySource.addListener(configChangeListener);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}
