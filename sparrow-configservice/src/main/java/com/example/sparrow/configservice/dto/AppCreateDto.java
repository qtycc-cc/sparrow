package com.example.sparrow.configservice.dto;

import com.example.sparrow.configservice.enums.Format;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppCreateDto {
    @NotBlank(message = "App name must not be blank")
    private String name;
    @NotNull(message = "App config file must not be null")
    private Format format;
}