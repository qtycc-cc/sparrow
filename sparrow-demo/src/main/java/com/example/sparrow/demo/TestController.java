package com.example.sparrow.demo;

import com.example.sparrow.client.config.SparrowConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SparrowConfigProperties sparrowConfigProperties;

    @GetMapping
    public String test() {
      log.info("The firstConfig is {}", firstConfig);
      log.info("The sparrow config properties is {}", sparrowConfigProperties);
      return firstConfig;
    }
}
