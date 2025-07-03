package com.stdc.visual.dynamic.base.datasource.dto;

/**
 * @author: wang_jie
 * @data: 2022/5/16--19:15
 * @describe: 数据源类型 , 暂时只支持mysql数据源
 */
public enum DatasourceTypes {
    excel("excel", "excel", "", "", "", "", ""),
    mysql("mysql", "mysql", "com.mysql.jdbc.Driver", "`", "`", "'", "'"),
    tdengine("tdengine", "tdengine", "com.taosdata.jdbc.TSDBDriver", "`", "`", "'", "'"),
    kingbase("kingbase", "kingbase", "com.kingbase8.Driver", "", "", "'", "'"),
    gbase("gbase", "gbase", "com.gbasedbt.jdbc.Driver", "", "", "", ""),
    hive("hive", "hive", "org.apache.hive.jdbc.HiveDriver", "`", "`", "'", "'"),
    mariadb("mariadb", "mariadb", "com.mysql.jdbc.Driver", "`", "`", "'", "'"),
    ds_doris("ds_doris", "ds_doris", "com.mysql.jdbc.Driver", "`", "`", "'", "'"),
    pg("pg", "pg", "org.postgresql.Driver", "\"", "\"", "\"", "\""),
    sqlServer("sqlServer", "sqlServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "\"", "\"", "\"", "\""),
    de_doris("de_doris", "de_doris", "com.mysql.jdbc.Driver", "`", "`", "", ""),
    oracle("oracle", "oracle", "oracle.jdbc.driver.OracleDriver", "\"", "\"", "\"", "\""),
    mongo("mongo", "mongodb", "com.mongodb.jdbc.MongoDriver", "`", "`", "\"", "\""),
    ck("ch", "ch", "ru.yandex.clickhouse.ClickHouseDriver", "`", "`", "'", "'"),
    db2("db2", "db2", "com.ibm.db2.jcc.DB2Driver", "\"", "\"", "\"", "\""),
    es("es", "es", "", "\"", "\"", "\"", "\""),
    dm("dm", "dm", "dm.jdbc.driver.DmDriver", "`", "`", "'", "'"),
    redshift("redshift", "redshift", "org.postgresql.Driver", "\"", "\"", "\"", "\"");


    private String feature;
    private String desc;
    private String driver;
    private String keywordPrefix;
    private String keywordSuffix;
    private String aliasPrefix;
    private String aliasSuffix;

    DatasourceTypes(String feature, String desc, String driver, String keywordPrefix, String keywordSuffix, String aliasPrefix, String aliasSuffix) {
        this.feature = feature;
        this.desc = desc;
        this.driver = driver;
        this.keywordPrefix = keywordPrefix;
        this.keywordSuffix = keywordSuffix;
        this.aliasPrefix = aliasPrefix;
        this.aliasSuffix = aliasSuffix;
    }

    public String getFeature() {
        return feature;
    }

    public String getDesc() {
        return desc;
    }

    public String getDriver() {
        return driver;
    }

    public String getKeywordPrefix() {
        return keywordPrefix;
    }

    public String getKeywordSuffix() {
        return keywordSuffix;
    }

    public String getAliasPrefix() {
        return aliasPrefix;
    }

    public String getAliasSuffix() {
        return aliasSuffix;
    }
}
