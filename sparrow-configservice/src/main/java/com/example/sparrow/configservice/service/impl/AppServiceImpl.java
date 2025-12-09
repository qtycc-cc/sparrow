package com.example.sparrow.configservice.service.impl;

import com.example.sparrow.configservice.dto.AppCreateDto;
import com.example.sparrow.configservice.dto.AppUpdateDto;
import com.example.sparrow.configservice.entity.App;
import com.example.sparrow.configservice.entity.Config;
import com.example.sparrow.configservice.enums.Format;
import com.example.sparrow.configservice.exception.ResourceNotFoundException;
import com.example.sparrow.configservice.repository.AppRepository;
import com.example.sparrow.configservice.repository.ConfigRepository;
import com.example.sparrow.configservice.repository.ReleaseRepository;
import com.example.sparrow.configservice.service.AppService;
import com.example.sparrow.configservice.util.PropertyUtil;
import com.example.sparrow.configservice.vo.AppVo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AppServiceImpl implements AppService {
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ReleaseRepository releaseRepository;

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE =  new TypeReference<>() {};

    @Override
    public PagedModel<AppVo> page(Pageable pageable) {
        Page<AppVo> appVos = appRepository.findAll(pageable).map(this::toAppVo);
        return new  PagedModel<>(appVos);
    }

    @Override
    public AppVo findOne(Long id) {
        return appRepository.findById(id).map(this::toAppVo).orElseThrow(() -> new ResourceNotFoundException("App not found by id: " + id));
    }

    @Override
    public AppVo findOne(String name) {
        return appRepository.findByName(name).map(this::toAppVo).orElseThrow(() -> new ResourceNotFoundException("App not found by name: " + name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AppCreateDto dto) {
        App app = new App();
        BeanUtils.copyProperties(dto, app);
        app.setConfigFile("");
        appRepository.save(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, AppUpdateDto dto) {
        App app = appRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("App not found by id: " + id));
        Format format = app.getFormat();
        List<Config> configs = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<>();
        if (Objects.equals(format, Format.YAML)) {
            map = PropertyUtil.extractYaml(dto.getConfigFile());
        } else if (Objects.equals(format, Format.PROPERTIES)) {
            map = PropertyUtil.extractProperties(dto.getConfigFile());
        }
        for (var entry : map.entrySet()) {
            Config config = new Config();
            config.setAppId(app.getId());
            config.setItemKey(entry.getKey());
            config.setItemValue(String.valueOf(entry.getValue()));
            configs.add(config);
        }
        // Simplified by deleting all and save new configs
        configRepository.deleteByAppId(app.getId());
        configRepository.flush();
        configRepository.saveAll(configs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        appRepository.deleteById(id);
        configRepository.deleteByAppId(id);
        releaseRepository.deleteByAppId(id);
    }

    private AppVo toAppVo(App app) {
        AppVo appVo = new AppVo();
        BeanUtils.copyProperties(app, appVo);
        return appVo;
    }
}
