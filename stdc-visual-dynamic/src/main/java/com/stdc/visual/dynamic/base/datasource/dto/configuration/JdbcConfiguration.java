package com.stdc.visual.dynamic.base.datasource.dto.configuration;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdbcConfiguration {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String dataBase;
    private String schema;
    private String dataSourceType = "jdbc";
    //数据库驱动-默认是custom
    private String customDriver = "default";

    private int initialPoolSize = 5;
    private int minPoolSize = 5;
    private int maxPoolSize = 50;
    //查询超时时间
    private int queryTimeout = 30;
}
