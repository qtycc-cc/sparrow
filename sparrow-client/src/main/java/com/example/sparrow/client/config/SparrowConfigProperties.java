package com.example.sparrow.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sparrow")
public class SparrowConfigProperties {
    private Boolean enable;
    private String appName;
    private String serverUrl;
}