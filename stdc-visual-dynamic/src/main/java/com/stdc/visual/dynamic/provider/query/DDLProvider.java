package com.stdc.visual.dynamic.provider.query;

/**
 * @author: wang_jie
 * @data: 2022/5/17--16:38
 * @describe:
 */
public abstract class DDLProvider {
    public abstract String createView(String name, String viewSQL);

    public abstract String dropTable(String name);

    public abstract String dropView(String name);
}
