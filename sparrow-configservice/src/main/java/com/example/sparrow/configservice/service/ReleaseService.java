package com.example.sparrow.configservice.service;

import com.example.sparrow.configservice.vo.ReleaseVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface ReleaseService {
    PagedModel<ReleaseVo> page(Long namespaceId, Pageable pageable);
    ReleaseVo findOne(Long id);
    void rollback(Long namespaceId, Long toId);
    void release(Long namespaceId);
}
