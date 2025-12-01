package com.example.sparrow.client.spring.processor;

import com.example.sparrow.client.infrastructure.SparrowInjector;
import com.example.sparrow.client.spring.PlaceholderHelper;
import com.example.sparrow.client.spring.SpringValue;
import com.example.sparrow.client.spring.SpringValueRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

@Slf4j
public class SpringValueProcessor extends AbstractValueProcessor implements BeanFactoryAware {
    private BeanFactory beanFactory;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;

    public SpringValueProcessor() {
        this.placeholderHelper = SparrowInjector.getInstance(PlaceholderHelper.class);
        this.springValueRegistry = SparrowInjector.getInstance(SpringValueRegistry.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    protected void processField(Object bean, String beanName, Field field) {
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());
        if (keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, field, false);
            springValueRegistry.register(beanFactory, key, springValue);
            log.debug("Monitoring {}", springValue);
        }
    }

    @Override
    protected void processMethod(Object bean, String beanName, Method method) {
        Value value = method.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        if (method.getAnnotation(Bean.class) != null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            log.error("Ignore @Value setter {}.{}, expecting 1 parameter, actual {} parameters",
                    bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
            return;
        }
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());

        if (keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, method, false);
            springValueRegistry.register(beanFactory, key, springValue);
            log.debug("Monitoring {}", springValue);
        }
    }
}
