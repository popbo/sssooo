package com.stdc.visual.dynamic.base.datasource.dto.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedshiftConfigration extends JdbcConfiguration {

    private String driver = "com.amazon.redshift.jdbc42.Driver";

    public String getJdbc() {
        // 连接参数先写死，后边要把编码、时区等参数放到数据源的设置中
        return "jdbc:redshift://HOSTNAME:PORT/DATABASE"
                .replace("HOSTNAME", getHost().trim())
                .replace("PORT", getPort().toString().trim())
                .replace("DATABASE", getDataBase().trim());
    }
}