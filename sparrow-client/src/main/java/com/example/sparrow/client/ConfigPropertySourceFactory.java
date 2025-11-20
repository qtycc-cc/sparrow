package com.example.sparrow.client;

import java.util.ArrayList;
import java.util.List;

public class ConfigPropertySourceFactory {
    private final List<ConfigPropertySource> configPropertySources = new ArrayList<>();

    public ConfigPropertySource get(String name) {
        ConfigPropertySource source = new ConfigPropertySource(name, new RemoteConfigRepository());
        configPropertySources.add(source);
        return source;
    }

    public List<ConfigPropertySource> getAll() {
        return configPropertySources;
    }
}
