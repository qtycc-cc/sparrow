package com.example.sparrow.client.spring.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

public class SparrowConfigEnvironmentProcessor implements EnvironmentPostProcessor, Ordered {
    public static final String[] SPARROW_SYSTEM_PROPERTIES = new String[]{
            "sparrow.appName",
            "sparrow.serverUrl"
    };

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        for (String key : SPARROW_SYSTEM_PROPERTIES) {
            if (System.getProperty(key) != null) {
                continue;
            }
            String value = environment.getProperty(key);
            if (!StringUtils.hasLength(value)) {
                return;
            }
            System.setProperty(key, value);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
