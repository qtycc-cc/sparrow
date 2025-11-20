package com.example.sparrow.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConfigChangeEvent {
    private List<ConfigChange> changes;
}
