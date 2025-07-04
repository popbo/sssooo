package com.stdc.visual.dynamic.base.datasource.dto.configuration;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsConfiguration {
    private String url;
    private String esUsername;
    private String esPassword;
    private String version;
    private String uri;
    private String dataSourceType = "es";
}
