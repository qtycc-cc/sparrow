package com.example.sparrow.client;

import org.springframework.core.env.EnumerablePropertySource;

public class ConfigPropertySource extends EnumerablePropertySource<RemoteConfigRepository> {
    public ConfigPropertySource(String name, RemoteConfigRepository source) {
        super(name, source);
    }

    @Override
    public String[] getPropertyNames() {
        return this.source.getPropertyNames();
    }

    @Override
    public Object getProperty(String name) {
        return this.source.getProperty(name, null);
    }

    public void addListener(ConfigChangeListener listener) {
        this.source.addListener(listener);
    }
}
