package com.stdc.visual.dynamic.base.datasource.dto.configuration;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: wang_jie
 * @data: 2022/11/21--10:36
 * @describe:
 */
@Getter
@Setter
public class GbaseConfiguration extends JdbcConfiguration {

    private String driver = "com.gbasedbt.jdbc.Driver";
    private String extraParams = "GBASEDBTSERVER={server}";

    public String getJdbc() {
        return "jdbc:gbasedbt-sqli://HOSTNAME:PORT/DATABASE:EXTRA_PARAMS"
                .replace("HOSTNAME", getHost().trim())
                .replace("PORT", getPort().toString().trim())
                .replace("DATABASE", getDataBase().trim())
                .replace("EXTRA_PARAMS", getExtraParams().trim());
    }

}
