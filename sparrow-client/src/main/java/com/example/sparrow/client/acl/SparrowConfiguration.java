package com.example.sparrow.client.acl;

import lombok.Data;

import java.util.Properties;

@Data
public class SparrowConfiguration {
    private Long appId;
    private Long releaseId;
    private Properties configuration;
}
