package com.example.sparrow.configservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppDto {
    @NotBlank(message = "App name must not be blank")
    String name;
}