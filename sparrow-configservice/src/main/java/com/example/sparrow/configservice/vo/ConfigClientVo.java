package com.example.sparrow.configservice.vo;

import lombok.Data;

import java.util.Properties;

@Data
public class ConfigClientVo {
    private Long namespaceId;
    private Long releaseId;
    private Properties configuration;
}
