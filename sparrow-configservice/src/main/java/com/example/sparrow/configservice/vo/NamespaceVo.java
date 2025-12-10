package com.example.sparrow.configservice.vo;

import com.example.sparrow.configservice.enums.Format;
import lombok.Data;

@Data
public class NamespaceVo {
    private Long id;
    private Long timeCreate;
    private Long timeUpdate;
    private String name;
    private Format format;
    private String configFile;
}
