package com.example.sparrow.configservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// TODO
@Deprecated
@Data
public class ConfigDto {
    @NotBlank(message = "Item key must not be blank")
    private String itemKey;
    @NotNull(message = "Did you mean ''(empty string)?")
    private String itemValue;
}
