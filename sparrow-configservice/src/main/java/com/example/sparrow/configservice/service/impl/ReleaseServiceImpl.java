package com.example.sparrow.configservice.service.impl;

import com.example.sparrow.configservice.entity.Config;
import com.example.sparrow.configservice.entity.Release;
import com.example.sparrow.configservice.event.ReleaseEvent;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.ConfigRepository;
import com.example.sparrow.configservice.repository.NamespaceRepository;
import com.example.sparrow.configservice.repository.ReleaseRepository;
import com.example.sparrow.configservice.service.ReleaseService;
import com.example.sparrow.configservice.util.TxUtils;
import com.example.sparrow.configservice.vo.ReleaseVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReleaseServiceImpl implements ReleaseService {
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private NamespaceRepository namespaceRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PagedModel<ReleaseVo> page(Long namespaceId, Pageable pageable) {
        Page<ReleaseVo> releaseVos = releaseRepository.findByNamespaceId(namespaceId, pageable).map(this::toReleaseVo);
        return new PagedModel<>(releaseVos);
    }

    @Override
    public ReleaseVo findOne(Long id) {
        return releaseRepository.findById(id).map(this::toReleaseVo).orElseThrow(() -> new ResourceNotFoundException("Release not found by id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(Long namespaceId, Long toId) {
        Release latest = releaseRepository.findFirstByNamespaceIdOrderByIdDesc(namespaceId).orElseThrow(() -> new IllegalStateException("Latest release not found"));
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
        release.setNamespaceId(namespaceId);
        release.setConfigSnapshot(toRelease.getConfigSnapshot());
        releaseRepository.save(release);
        TxUtils.afterCommit(() -> applicationEventPublisher.publishEvent(new ReleaseEvent(this, namespaceId, release.getId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(Long namespaceId) {
        namespaceRepository.findById(namespaceId).orElseThrow(() -> new ResourceNotFoundException("App not found"));
        List<Config> configs = configRepository.findByNamespaceId(namespaceId);
        Release release = new Release();
        release.setNamespaceId(namespaceId);
        // avoid add backslash
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(Config::getItemKey, Config::getItemValue, (k1, k2) -> k1));
        try {
            release.setConfigSnapshot(objectMapper.writeValueAsString(configMap));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        releaseRepository.save(release);
        TxUtils.afterCommit(() -> applicationEventPublisher.publishEvent(new ReleaseEvent(this, namespaceId, release.getId())));
    }

    private ReleaseVo toReleaseVo(Release release) {
        ReleaseVo releaseVo = new ReleaseVo();
        BeanUtils.copyProperties(release, releaseVo);
        return releaseVo;
    }
}
