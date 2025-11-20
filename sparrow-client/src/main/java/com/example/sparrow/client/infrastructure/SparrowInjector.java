package com.example.sparrow.client.infrastructure;

import com.example.sparrow.client.ConfigPropertySourceFactory;
import com.example.sparrow.client.spring.PlaceholderHelper;
import com.example.sparrow.client.spring.SpringValueRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.springframework.web.client.RestTemplate;

public class SparrowInjector {
    private static volatile Injector injector;

    private SparrowInjector() {}

    private static Injector createInjector() {
        if (injector == null) {
            synchronized (SparrowInjector.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new SparrowModule());
                }
            }
        }
        return injector;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return createInjector().getInstance(clazz);
    }

    static class SparrowModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(RestTemplate.class).in(Singleton.class);
            bind(SpringValueRegistry.class).in(Singleton.class);
            bind(PlaceholderHelper.class).in(Singleton.class);
            bind(ConfigPropertySourceFactory.class).in(Singleton.class);
        }
    }
}
