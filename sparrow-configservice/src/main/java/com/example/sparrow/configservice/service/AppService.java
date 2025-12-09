package com.example.sparrow.configservice.service;

import com.example.sparrow.configservice.dto.AppCreateDto;
import com.example.sparrow.configservice.dto.AppUpdateDto;
import com.example.sparrow.configservice.vo.AppVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface AppService {
    PagedModel<AppVo> page(Pageable pageable);
    AppVo findOne(Long id);
    AppVo findOne(String name);
    void create(AppCreateDto dto);
    void update(Long id, AppUpdateDto dto);
    void delete(Long id);
}
