package com.example.sparrow.client.spring;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringValueRegistry {
    private final Map<BeanFactory, Multimap<String, SpringValue>> registry = new ConcurrentHashMap<>();

    public void register(BeanFactory beanFactory, String key, SpringValue springValue) {
        if (!registry.containsKey(beanFactory)) {
            synchronized (SpringValueRegistry.class) {
                if (!registry.containsKey(beanFactory)) {
                    registry.put(beanFactory, Multimaps.synchronizedListMultimap(LinkedListMultimap.create()));
                }
            }
        }
        registry.get(beanFactory).put(key, springValue);
    }

    public Collection<SpringValue> get(BeanFactory beanFactory, String key) {
        Multimap<String, SpringValue> beanFactorySpringValues = registry.get(beanFactory);
        if (beanFactorySpringValues == null) {
            return Collections.emptyList();
        }
        return beanFactorySpringValues.get(key);
    }
}
