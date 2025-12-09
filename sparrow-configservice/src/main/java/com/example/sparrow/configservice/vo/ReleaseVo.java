package com.example.sparrow.configservice.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ReleaseVo {
    private Long id;
    private Long timeCreate;
    private Long timeUpdate;
    private Map<String, Object> configSnapshotView;
}
