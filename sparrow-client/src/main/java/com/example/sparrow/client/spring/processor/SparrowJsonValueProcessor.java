package com.example.sparrow.client.spring.processor;

import com.example.sparrow.client.annotation.SparrowJsonValue;
import com.example.sparrow.client.infrastructure.SparrowInjector;
import com.example.sparrow.client.spring.PlaceholderHelper;
import com.example.sparrow.client.spring.SpringValue;
import com.example.sparrow.client.spring.SpringValueRegistry;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

@Slf4j
public class SparrowJsonValueProcessor extends AbstractValueProcessor implements BeanFactoryAware {
    private ConfigurableBeanFactory beanFactory;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SparrowJsonValueProcessor() {
        this.placeholderHelper = SparrowInjector.getInstance(PlaceholderHelper.class);
        this.springValueRegistry = SparrowInjector.getInstance(SpringValueRegistry.class);
    }

    @Override
    protected void processField(Object bean, String beanName, Field field) {
        SparrowJsonValue sparrowJsonValue = field.getAnnotation(SparrowJsonValue.class);
        // init property
        if (sparrowJsonValue == null) {
            return;
        }
        Object propertyValue = placeholderHelper
                .resolvePropertyValue(beanFactory, beanName, sparrowJsonValue.value());
        if (!(propertyValue instanceof String)) {
            return;
        }
        field.setAccessible(true);
        JavaType type = objectMapper.getTypeFactory().constructType(field.getGenericType());
        try {
            field.set(bean, objectMapper.readValue((String) propertyValue, type));
        } catch (Throwable ex) {
            log.error("Error when parsing json from {} to {}", propertyValue, type);
        }
        // registry for update
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(sparrowJsonValue.value());
        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, sparrowJsonValue.value(), bean, beanName, field, true);
            springValueRegistry.register(beanFactory, key, springValue);
            log.debug("Monitoring {}", springValue);
        }
    }

    @Override
    protected void processMethod(Object bean, String beanName, Method method) {
        SparrowJsonValue sparrowJsonValue = method.getAnnotation(SparrowJsonValue.class);
        // init property
        if (sparrowJsonValue == null) {
            return;
        }
        Object propertyValue = placeholderHelper
                .resolvePropertyValue(beanFactory, beanName, sparrowJsonValue.value());
        if (!(propertyValue instanceof String)) {
            return;
        }
        Type[] types = method.getGenericParameterTypes();
        if (types.length != 1) {
            log.error("Ignore @SparrowJsonValue setter {}.{}, expecting 1 parameter, actual {} parameters",
                    bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
            return;
        }
        JavaType type = objectMapper.getTypeFactory().constructType(types[0]);
        method.setAccessible(true);
        try {
            method.invoke(bean, objectMapper.readValue((String) propertyValue, type));
        } catch (Throwable ex) {
            log.error("Error when parsing json from {} to {}", propertyValue, type);
        }
        // registry for update
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(sparrowJsonValue.value());
        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, sparrowJsonValue.value(), bean, beanName, method, false);
            springValueRegistry.register(beanFactory, key, springValue);
            log.debug("Monitoring {}", springValue);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }
}
