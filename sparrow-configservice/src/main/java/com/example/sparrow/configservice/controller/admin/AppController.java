package com.example.sparrow.configservice.controller.admin;

import com.example.sparrow.configservice.dto.AppCreateDto;
import com.example.sparrow.configservice.service.AppService;
import com.example.sparrow.configservice.vo.AppVo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/admin/app")
public class AppController {
    @Autowired
    private AppService appService;

    @GetMapping
    public ResponseEntity<PagedModel<AppVo>> page(Pageable pageable) {
        return ResponseEntity.ok(appService.page(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppVo> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(appService.findOne(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AppVo> getOne(@PathVariable String name) {
        return ResponseEntity.ok(appService.findOne(name));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid AppCreateDto dto) {
        appService.create(dto);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appService.delete(id);
        return ResponseEntity.ok(null);
    }
}
