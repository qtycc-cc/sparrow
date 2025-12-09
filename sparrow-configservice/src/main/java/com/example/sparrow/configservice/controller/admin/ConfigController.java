package com.example.sparrow.configservice.controller.admin;

import com.example.sparrow.configservice.dto.ConfigDto;
import com.example.sparrow.configservice.entity.Config;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.AppRepository;
import com.example.sparrow.configservice.repository.ConfigRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

// TODO
@Deprecated
@CrossOrigin
@RestController
@RequestMapping("/admin/config")
public class ConfigController {
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private AppRepository appRepository;

    @GetMapping("/appId/{appId}")
    public ResponseEntity<PagedModel<Config>> page(@PathVariable Long appId, Pageable pageable) {
        Page<Config> configs = configRepository.findByAppId(appId, pageable);
        return ResponseEntity.ok(new PagedModel<>(configs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Config> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(configRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Config not found")));
    }

    @PostMapping("/appId/{appId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> create(@PathVariable Long appId, @RequestBody @Valid ConfigDto configDto) {
        appRepository.findById(appId).orElseThrow(() -> new ResourceNotFoundException("App not found"));
        Config config = new Config();
        config.setAppId(appId);
        BeanUtils.copyProperties(configDto, config);
        configRepository.save(config);
        return ResponseEntity.ok(null);
    }

    @PatchMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> patch(@PathVariable Long id, @RequestBody @Valid ConfigDto configDto) {
        Config config = configRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Config not found"));
        // This will save to db when tx commit
        config.setItemKey(configDto.getItemKey());
        config.setItemValue(configDto.getItemValue());
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        configRepository.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
