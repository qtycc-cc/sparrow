package com.example.sparrow.configservice.controller.admin;

import com.example.sparrow.configservice.dto.NamespaceCreateDto;
import com.example.sparrow.configservice.dto.NamespaceUpdateDto;
import com.example.sparrow.configservice.service.NamespaceService;
import com.example.sparrow.configservice.vo.NamespaceVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/admin/namespace")
public class NamespaceController {
    @Autowired
    private NamespaceService namespaceService;

    @GetMapping
    public ResponseEntity<PagedModel<NamespaceVo>> page(Pageable pageable) {
        return ResponseEntity.ok(namespaceService.page(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NamespaceVo> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(namespaceService.findOne(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<NamespaceVo> getOne(@PathVariable String name) {
        return ResponseEntity.ok(namespaceService.findOne(name));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid NamespaceCreateDto dto) {
        namespaceService.create(dto);
        return ResponseEntity.ok(null);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid NamespaceUpdateDto dto) {
        namespaceService.update(id, dto);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        namespaceService.delete(id);
        return ResponseEntity.ok(null);
    }
}
