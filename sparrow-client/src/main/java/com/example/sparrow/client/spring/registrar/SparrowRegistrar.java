package com.example.sparrow.client.spring.registrar;

import com.example.sparrow.client.spring.processor.SparrowJsonValueProcessor;
import com.example.sparrow.client.spring.processor.SparrowPropertySourceProcessor;
import com.example.sparrow.client.spring.processor.SpringValueProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Map;

@Slf4j
public class SparrowRegistrar implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registry(registry, PropertySourcesPlaceholderConfigurer.class, Map.of("order", 0));
        registry(registry, SparrowPropertySourceProcessor.class, null);
        registry(registry, SpringValueProcessor.class, null);
        registry(registry, SparrowJsonValueProcessor.class, null);
    }

    private void registry(BeanDefinitionRegistry registry, Class<?> beanClass, Map<String, Object> extra) {
        if (registry.containsBeanDefinition(beanClass.getName())) {
            return;
        }
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        if (extra != null) {
            for (Map.Entry<String, Object> entry : extra.entrySet()) {
                beanDefinition.getPropertyValues().add(entry.getKey(), entry.getValue());
            }
        }
        registry.registerBeanDefinition(beanClass.getName(), beanDefinition);
    }
}
