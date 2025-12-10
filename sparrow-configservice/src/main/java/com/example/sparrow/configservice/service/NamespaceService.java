package com.example.sparrow.configservice.service;

import com.example.sparrow.configservice.dto.NamespaceCreateDto;
import com.example.sparrow.configservice.dto.NamespaceUpdateDto;
import com.example.sparrow.configservice.vo.NamespaceVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface NamespaceService {
    PagedModel<NamespaceVo> page(Pageable pageable);
    NamespaceVo findOne(Long id);
    NamespaceVo findOne(String name);
    void create(NamespaceCreateDto dto);
    void update(Long id, NamespaceUpdateDto dto);
    void delete(Long id);
}
