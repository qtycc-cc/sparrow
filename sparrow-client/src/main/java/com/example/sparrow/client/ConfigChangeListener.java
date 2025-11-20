package com.example.sparrow.client;

import com.example.sparrow.client.infrastructure.SparrowInjector;
import com.example.sparrow.client.model.ConfigChange;
import com.example.sparrow.client.model.ConfigChangeEvent;
import com.example.sparrow.client.spring.PlaceholderHelper;
import com.example.sparrow.client.spring.SpringValue;
import com.example.sparrow.client.spring.SpringValueRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ConfigChangeListener {
    private final ConfigurableBeanFactory beanFactory;
    private final TypeConverter typeConverter;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;

    public ConfigChangeListener(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.typeConverter = beanFactory.getTypeConverter();
        this.placeholderHelper = SparrowInjector.getInstance(PlaceholderHelper.class);
        this.springValueRegistry = SparrowInjector.getInstance(SpringValueRegistry.class);
    }

    public void onChange(ConfigChangeEvent configChangeEvent) {
        Set<String> keys = configChangeEvent.getChanges().stream().map(ConfigChange::getPropertyName).collect(Collectors.toSet());
        if (keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            Collection<SpringValue> targetValues = springValueRegistry.get(beanFactory, key);
            if (targetValues == null || targetValues.isEmpty()) {
                continue;
            }

            for (SpringValue value : targetValues) {
                try {
                    updateSpringValue(value);
                    log.info("Update config change event success, spring value is: {}", value);
                } catch(Exception ex) {
                    log.warn("Update config change event failed, spring value is: {}", value, ex);
                }
            }
        }
    }

    private void updateSpringValue(SpringValue springValue) throws InvocationTargetException, IllegalAccessException {
        Object value = placeholderHelper
                .resolvePropertyValue(beanFactory, springValue.getBeanName(), springValue.getPlaceholder());
        if (springValue.isField()) {
            value = this.typeConverter
                    .convertIfNecessary(value, springValue.getTargetType().getClass(), springValue.getField());
        } else {
            value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType().getClass(),
                    springValue.getMethodParameter());
        }
        springValue.update(value);
    }
}
