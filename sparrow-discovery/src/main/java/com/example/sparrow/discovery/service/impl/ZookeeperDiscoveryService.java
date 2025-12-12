package com.example.sparrow.discovery.service.impl;

import com.example.sparrow.discovery.service.DiscoveryService;
import com.example.sparrow.discovery.vo.ServiceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ZookeeperDiscoveryService implements DiscoveryService {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public List<ServiceVo> getInstances(String serviceId) {
        return discoveryClient.getInstances(serviceId).stream().map(this::toServiceVo).toList();
    }

    @Override
    public ServiceVo loadBalance(String serviceId) {
        var list =  discoveryClient.getInstances(serviceId);
        // Simplified by shuffling
        Collections.shuffle(list);
        return toServiceVo(list.get(0));
    }

    private ServiceVo toServiceVo(ServiceInstance instance) {
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setServiceId(instance.getServiceId());
        serviceVo.setInstanceId(instance.getInstanceId());
        serviceVo.setUrl("http://" + instance.getHost() + ":" + instance.getPort() + "/");
        return serviceVo;
    }
}
