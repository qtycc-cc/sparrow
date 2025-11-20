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
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {
    private BeanFactory beanFactory;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;

    public SpringValueProcessor() {
        this.placeholderHelper = SparrowInjector.getInstance(PlaceholderHelper.class);
        this.springValueRegistry = SparrowInjector.getInstance(SpringValueRegistry.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        List<Field> fields = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, fields::add);
        List<Method> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(clazz, methods::add);

        for (Field field : fields) {
            Value value = field.getAnnotation(Value.class);
            if (value == null) {
                continue;
            }
            Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());
            if (keys.isEmpty()) {
                log.debug("Can not find place ho");
                continue;
            }
            for (String key : keys) {
                SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, field);
                springValueRegistry.register(beanFactory, key, springValue);
                log.debug("Monitoring {}", springValue);
            }
        }

        for (Method method : methods) {
            Value value = method.getAnnotation(Value.class);
            if (value == null) {
                continue;
            }
            if (method.getAnnotation(Bean.class) != null) {
                continue;
            }
            if (method.getParameterTypes().length != 1) {
                log.error("Ignore @Value setter {}.{}, expecting 1 parameter, actual {} parameters",
                        bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
                continue;
            }
            Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());

            if (keys.isEmpty()) {
                continue;
            }
            for (String key : keys) {
                SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, method);
                springValueRegistry.register(beanFactory, key, springValue);
                log.debug("Monitoring {}", springValue);
            }
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }
}
