package com.example.sparrow.client.factory;

import com.example.sparrow.client.ConfigPropertySource;
import com.example.sparrow.client.RemoteConfigRepository;

import java.util.ArrayList;
import java.util.List;

public class ConfigPropertySourceFactory {
    private final List<ConfigPropertySource> configPropertySources = new ArrayList<>();

    public ConfigPropertySource get(String name) {
        ConfigPropertySource source = new ConfigPropertySource(name, new RemoteConfigRepository(name));
        configPropertySources.add(source);
        return source;
    }

    public List<ConfigPropertySource> getAll() {
        return configPropertySources;
    }
}
