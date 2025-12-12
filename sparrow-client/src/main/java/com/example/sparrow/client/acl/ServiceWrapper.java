package com.example.sparrow.client.acl;

import lombok.Data;

@Data
public class ServiceWrapper {
    private String serviceId;
    private String instanceId;
    private String url;
}
