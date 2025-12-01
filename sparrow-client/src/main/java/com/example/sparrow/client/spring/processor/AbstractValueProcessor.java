package com.example.sparrow.client.spring.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValueProcessor implements BeanPostProcessor, PriorityOrdered {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Field field : findAllFields(clazz)) {
            processField(bean, beanName, field);
        }
        for (Method method : findAllMethods(clazz)) {
            processMethod(bean, beanName, method);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }

    protected abstract void processField(Object bean, String beanName, Field field);
    protected abstract void processMethod(Object bean, String beanName, Method method);

    private List<Field> findAllFields(Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, fields::add);
        return fields;
    }

    private List<Method> findAllMethods(Class<?> clazz) {
        final List<Method> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(clazz, methods::add);
        return methods;
    }
}
