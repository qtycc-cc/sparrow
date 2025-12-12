package com.example.sparrow.discovery.service;

import com.example.sparrow.discovery.vo.ServiceVo;

import java.util.List;

public interface DiscoveryService {
    List<ServiceVo> getInstances(String serviceId);
    ServiceVo loadBalance(String serviceId);
}
