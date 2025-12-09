package com.example.sparrow.configservice.util;

import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {
    public static Map<String, Object> extractYaml(String yamlString) {
        Yaml yaml = new Yaml();
        Map<String, Object> source = yaml.load(yamlString);
        Map<String, Object> result = new LinkedHashMap<>();
        flatten("", source, result);
        return result;
    }

    @SneakyThrows
    public static Map<String, Object> extractProperties(String propertiesString) {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(propertiesString.getBytes()));
        Map<String, Object> result = new LinkedHashMap<>();
        for (String name : properties.stringPropertyNames()) {
            result.put(name, properties.getProperty(name));
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void flatten(String prefix, Map<String, Object> source, Map<String, Object> target) {
        source.forEach((key, value) -> {
            String fullKey = prefix + key;
            if (value instanceof Map) {
                flatten(fullKey + ".", (LinkedHashMap) value, target);
            } else if (value instanceof List list) {
                for (int i = 0; i < list.size(); i++) {
                    String itemKey = String.format("%s[%d]", fullKey, i);
                    Object itemValue = list.get(i);
                    if (itemValue instanceof Map) {
                        flatten(itemKey + ".", (LinkedHashMap) itemValue, target);
                    } else {
                        target.put(itemKey, itemValue);
                    }
                }
            } else {
                target.put(fullKey, value);
            }
        });
    }
}
