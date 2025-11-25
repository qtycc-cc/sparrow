package com.example.sparrow.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("sparrow")
public class SparrowConfigProperties {
    private Boolean enable;
    private String appName;
    private String serverUrl;
}