package com.example.sparrow.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigChange {
    private String propertyName;
    private String oldValue;
    private String newValue;
    private ConfigChangeType changeType;

    public enum ConfigChangeType {
        CREATE, MODIFY, DELETE
    }
}
