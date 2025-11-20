package com.example.sparrow.client.config;

import com.example.sparrow.client.spring.registrar.SparrowRegistrar;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty("sparrow.enable")
@EnableConfigurationProperties(SparrowConfigProperties.class)
public class SparrowAutoConfiguration {
    @Bean
    public SparrowRegistrar sparrowRegistrar() {
        return new SparrowRegistrar();
    }
}
