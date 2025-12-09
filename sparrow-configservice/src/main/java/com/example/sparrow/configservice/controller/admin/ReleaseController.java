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

    @GetMapping("/appId/{appId}")
    public ResponseEntity<PagedModel<ReleaseVo>> page(@PathVariable Long appId, Pageable pageable) {
        return ResponseEntity.ok(releaseService.page(appId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReleaseVo> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(releaseService.findOne(id));
    }

    @PostMapping("/appId/{appId}/rollback")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> rollback(@PathVariable Long appId, @RequestParam Long toId) {
        releaseService.rollback(appId, toId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/appId/{appId}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> release(@PathVariable Long appId) {
        releaseService.release(appId);
        return ResponseEntity.ok(null);
    }
}
