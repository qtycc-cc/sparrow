package com.example.sparrow.configservice.controller.admin;

import com.example.sparrow.configservice.service.ReleaseService;
import com.example.sparrow.configservice.vo.ReleaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/admin/release")
public class ReleaseController {
    @Autowired
    private ReleaseService releaseService;

    @GetMapping("/namespaceId/{namespaceId}")
    public ResponseEntity<PagedModel<ReleaseVo>> page(@PathVariable Long namespaceId, Pageable pageable) {
        return ResponseEntity.ok(releaseService.page(namespaceId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReleaseVo> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(releaseService.findOne(id));
    }

    @PostMapping("/namespaceId/{namespaceId}/rollback")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> rollback(@PathVariable Long namespaceId, @RequestParam Long toId) {
        releaseService.rollback(namespaceId, toId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/namespaceId/{namespaceId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> release(@PathVariable Long namespaceId) {
        releaseService.release(namespaceId);
        return ResponseEntity.ok(null);
    }
}
