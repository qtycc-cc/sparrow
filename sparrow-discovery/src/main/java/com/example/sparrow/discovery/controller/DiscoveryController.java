package com.example.sparrow.discovery.controller;

import com.example.sparrow.discovery.service.DiscoveryService;
import com.example.sparrow.discovery.vo.ServiceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {
    @Autowired
    private DiscoveryService discoveryService;

    @GetMapping("/{serviceId}/all")
    public ResponseEntity<List<ServiceVo>> getInstances(@PathVariable String serviceId) {
        return ResponseEntity.ok(discoveryService.getInstances(serviceId));
    }

    @GetMapping("/{serviceId}/loadBalance")
    public ResponseEntity<ServiceVo> loadBalance(@PathVariable String serviceId) {
        return ResponseEntity.ok(discoveryService.loadBalance(serviceId));
    }
}
