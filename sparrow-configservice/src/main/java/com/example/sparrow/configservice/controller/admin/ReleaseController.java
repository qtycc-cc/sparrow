package com.example.sparrow.configservice.controller.admin;

import com.example.sparrow.configservice.entity.Config;
import com.example.sparrow.configservice.entity.Release;
import com.example.sparrow.configservice.event.ReleaseEvent;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.AppRepository;
import com.example.sparrow.configservice.repository.ConfigRepository;
import com.example.sparrow.configservice.repository.ReleaseRepository;
import com.example.sparrow.configservice.util.TxUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/admin/release")
public class ReleaseController {
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/appId/{appId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> release(@PathVariable Long appId) throws JsonProcessingException {
        appRepository.findById(appId).orElseThrow(() -> new ResourceNotFoundException("App not found"));
        List<Config> configs = configRepository.findByAppId(appId);
        Release release = new Release();
        release.setAppId(appId);
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(Config::getItemKey, Config::getItemValue, (k1, k2) -> k1));
        release.setConfigSnapshot(objectMapper.writeValueAsString(configMap));
        releaseRepository.save(release);
        TxUtils.afterCommit(() -> applicationEventPublisher.publishEvent(new ReleaseEvent(this, appId, release.getId())));
        return ResponseEntity.ok(null);
    }
}
