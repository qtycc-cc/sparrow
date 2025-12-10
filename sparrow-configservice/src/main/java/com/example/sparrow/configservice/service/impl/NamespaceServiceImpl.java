package com.example.sparrow.configservice.service.impl;

import com.example.sparrow.configservice.dto.NamespaceCreateDto;
import com.example.sparrow.configservice.dto.NamespaceUpdateDto;
import com.example.sparrow.configservice.entity.Config;
import com.example.sparrow.configservice.entity.Namespace;
import com.example.sparrow.configservice.enums.Format;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.ConfigRepository;
import com.example.sparrow.configservice.repository.NamespaceRepository;
import com.example.sparrow.configservice.repository.ReleaseRepository;
import com.example.sparrow.configservice.service.NamespaceService;
import com.example.sparrow.configservice.util.PropertyUtil;
import com.example.sparrow.configservice.vo.NamespaceVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NamespaceServiceImpl implements NamespaceService {
    @Autowired
    private NamespaceRepository namespaceRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ReleaseRepository releaseRepository;

    @Override
    public PagedModel<NamespaceVo> page(Pageable pageable) {
        Page<NamespaceVo> appVos = namespaceRepository.findAll(pageable).map(this::toNamespaceVo);
        return new  PagedModel<>(appVos);
    }

    @Override
    public NamespaceVo findOne(Long id) {
        return namespaceRepository.findById(id).map(this::toNamespaceVo).orElseThrow(() -> new ResourceNotFoundException("Namespace not found by id: " + id));
    }

    @Override
    public NamespaceVo findOne(String name) {
        return namespaceRepository.findByName(name).map(this::toNamespaceVo).orElseThrow(() -> new ResourceNotFoundException("Namespace not found by name: " + name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(NamespaceCreateDto dto) {
        Namespace namespace = new Namespace();
        BeanUtils.copyProperties(dto, namespace);
        namespace.setConfigFile("");
        namespaceRepository.save(namespace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, NamespaceUpdateDto dto) {
        Namespace namespace = namespaceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Namespace not found by id: " + id));
        Format format = namespace.getFormat();
        List<Config> configs = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<>();
        if (Objects.equals(format, Format.YAML)) {
            map = PropertyUtil.extractYaml(dto.getConfigFile());
        } else if (Objects.equals(format, Format.PROPERTIES)) {
            map = PropertyUtil.extractProperties(dto.getConfigFile());
        }
        for (var entry : map.entrySet()) {
            Config config = new Config();
            config.setNamespaceId(namespace.getId());
            config.setItemKey(entry.getKey());
            config.setItemValue(String.valueOf(entry.getValue()));
            configs.add(config);
        }
        namespace.setConfigFile(dto.getConfigFile());
        // Simplified by deleting all and save new configs
        configRepository.deleteByNamespaceId(namespace.getId());
        configRepository.flush();
        configRepository.saveAll(configs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        namespaceRepository.deleteById(id);
        configRepository.deleteByNamespaceId(id);
        releaseRepository.deleteByNamespaceId(id);
    }

    private NamespaceVo toNamespaceVo(Namespace namespace) {
        NamespaceVo namespaceVo = new NamespaceVo();
        BeanUtils.copyProperties(namespace, namespaceVo);
        return namespaceVo;
    }
}
