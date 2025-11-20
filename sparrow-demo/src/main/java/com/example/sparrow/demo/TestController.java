package com.example.sparrow.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class TestController {
    @Value("${first.item:default}")
    private String firstConfig;

    @GetMapping
    public String test() {
      log.info("The firstConfig is {}", firstConfig);
      return firstConfig;
    }
}
