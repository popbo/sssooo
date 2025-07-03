package com.stdc.visual.dynamic.base.datasource.dto.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DorisConfiguration extends MysqlConfiguration {

    private Integer httpPort;
}
