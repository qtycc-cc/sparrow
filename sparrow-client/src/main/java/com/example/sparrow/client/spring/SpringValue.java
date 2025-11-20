package com.example.sparrow.client.spring;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.MethodParameter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Getter
@Setter
public class SpringValue {
    private MethodParameter methodParameter;
    private Field field;
    private WeakReference<Object> beanRef;
    private String beanName;
    private String key;
    private String placeholder;
    private Type targetType;

    public SpringValue(String key, String placeholder, Object bean, String beanName, Field field) {
        this.beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.field = field;
        this.key = key;
        this.placeholder = placeholder;
        this.targetType = field.getGenericType();
    }

    public SpringValue(String key, String placeholder, Object bean, String beanName, Method method) {
        this.beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.methodParameter = new MethodParameter(method, 0);
        this.key = key;
        this.placeholder = placeholder;
        this.targetType = method.getGenericParameterTypes()[0];
    }

    public void update(Object newValue) throws IllegalAccessException, InvocationTargetException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }
        if (isField()) {
            field.setAccessible(true);
            field.set(bean, newValue);
        } else {
            methodParameter.getMethod().invoke(bean, newValue);
        }
    }

    public boolean isField() {
        return field != null;
    }

    boolean isTargetBeanValid() {
        return beanRef.get() != null;
    }

    @Override
    public String toString() {
        Object bean = beanRef.get();
        if (bean == null) {
            return "";
        }
        if (isField()) {
            return String
                    .format("key: %s, beanName: %s, field: %s.%s", key, beanName, bean.getClass().getName(), field.getName());
        }
        return String.format("key: %s, beanName: %s, method: %s.%s", key, beanName, bean.getClass().getName(),
                methodParameter.getMethod().getName());
    }
}
