package com.example.sparrow.configservice.vo;

import lombok.Data;

@Data
public class ReleaseVo {
    private Long id;
    private Long timeCreate;
    private Long timeUpdate;
    private String configSnapshot;
}
