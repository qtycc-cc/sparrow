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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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

    @GetMapping("/appId/{appId}")
    public ResponseEntity<PagedModel<Release>> page(@PathVariable Long appId, Pageable pageable) {
        Page<Release> releases = releaseRepository.findByAppId(appId, pageable);
        return ResponseEntity.ok(new PagedModel<>(releases));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Release> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(releaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Release not found")));
    }

    @PostMapping("/appId/{appId}/rollback")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> rollback(@PathVariable Long appId, @RequestParam Long toId) {
        Release latest = releaseRepository.findFirstByAppIdOrderByIdDesc(appId).orElseThrow(() -> new IllegalStateException("Latest release not found"));
        Release toRelease;
        if (Objects.equals(latest.getId(), toId)) {
            // indicate user want to roll back the latest release
            // so the target release is the second new release
            toRelease = releaseRepository.findFirstByIdLessThanOrderByIdDesc(latest.getId()).orElseThrow(() -> new ResourceNotFoundException("Target release not found"));
        } else {
            toRelease = releaseRepository.findById(toId).orElseThrow(() -> new ResourceNotFoundException("Target release not found"));
        }
        // create a new release with target's config snapshot
        Release release = new Release();
        release.setAppId(appId);
        release.setConfigSnapshot(toRelease.getConfigSnapshot());
        releaseRepository.save(release);
        TxUtils.afterCommit(() -> applicationEventPublisher.publishEvent(new ReleaseEvent(this, appId, release.getId())));
        return ResponseEntity.ok(null);
    }

    @PostMapping("/appId/{appId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> release(@PathVariable Long appId) throws JsonProcessingException {
        appRepository.findById(appId).orElseThrow(() -> new ResourceNotFoundException("App not found"));
        List<Config> configs = configRepository.findByAppId(appId);
        Release release = new Release();
        release.setAppId(appId);
        // avoid add backslash
        Map<String, Object> configMap = configs.stream().collect(Collectors.toMap(Config::getItemKey, config -> {
            String value = config.getItemValue();
            try {
                return objectMapper.readValue(value, Object.class);
            } catch(Exception ex) {
                log.warn("Error when parsing json {}", value);
                return value;
            }
        }, (k1, k2) -> k1));
        release.setConfigSnapshot(objectMapper.writeValueAsString(configMap));
        releaseRepository.save(release);
        TxUtils.afterCommit(() -> applicationEventPublisher.publishEvent(new ReleaseEvent(this, appId, release.getId())));
        return ResponseEntity.ok(null);
    }
}
