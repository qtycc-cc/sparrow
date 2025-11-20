package com.example.sparrow.configservice.controller.admin;

import com.example.sparrow.configservice.dto.AppDto;
import com.example.sparrow.configservice.entity.App;
import com.example.sparrow.configservice.repository.AppRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/admin/app")
public class AppController {
    @Autowired
    private AppRepository appRepository;

    @GetMapping
    public ResponseEntity<PagedModel<App>> page(Pageable pageable) {
        Page<App> apps = appRepository.findAll(pageable);
        return ResponseEntity.ok(new PagedModel<>(apps));
    }

    @GetMapping("/{id}")
    public ResponseEntity<App> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(appRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("App not found")));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<App> getOne(@PathVariable String name) {
        return ResponseEntity.ok(appRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("App not found")));
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> create(@RequestBody @Valid AppDto dto) {
        App app = new App();
        BeanUtils.copyProperties(dto, app);
        appRepository.save(app);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appRepository.deleteById(id);
        return ResponseEntity.ok(null);
    }
}
