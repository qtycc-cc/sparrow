package com.example.sparrow.configservice.controller.client;

import com.example.sparrow.configservice.entity.Namespace;
import com.example.sparrow.configservice.entity.Release;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.NamespaceRepository;
import com.example.sparrow.configservice.repository.ReleaseRepository;
import com.example.sparrow.configservice.vo.ConfigClientVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/client/config")
public class ConfigClientController {
    private static final TypeReference<Map<String, String>> type = new TypeReference<>() {};
    @Autowired
    private NamespaceRepository namespaceRepository;
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/namespaceName/{namespaceName}")
    public ResponseEntity<ConfigClientVo> loadConfigs(@PathVariable String namespaceName) throws JsonProcessingException {
        Namespace namespace = namespaceRepository.findByName(namespaceName).orElseThrow(() -> new ResourceNotFoundException("Namespace not found"));
        Optional<Release> releaseOptional = releaseRepository.findFirstByNamespaceIdOrderByIdDesc(namespace.getId());
        if (releaseOptional.isPresent()) {
            Release release = releaseOptional.get();
            ConfigClientVo configClientVo = new ConfigClientVo();
            Properties properties = new Properties();
            properties.putAll(objectMapper.readValue(release.getConfigSnapshot(), type));
            configClientVo.setNamespaceId(namespace.getId());
            configClientVo.setReleaseId(release.getId());
            configClientVo.setConfiguration(properties);
            return ResponseEntity.ok(configClientVo);
        }
        log.warn("Could not find any releases for this namespace: {}", namespaceName);
        throw new ResourceNotFoundException("Releases not find, can not extract config snapshots from releases");
    }
}
