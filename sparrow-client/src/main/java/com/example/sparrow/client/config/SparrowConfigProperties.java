package com.example.sparrow.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("sparrow")
public class SparrowConfigProperties {
    private Boolean enable;
    /**
     * 多个使用,隔开（不要空格）
     */
    private List<String> namespaceNames;
    private String serverUrl;
}