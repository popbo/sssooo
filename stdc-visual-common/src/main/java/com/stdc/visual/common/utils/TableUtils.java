package com.stdc.visual.common.utils;

/**
 * @author: wang_jie
 * @data: 2022/5/17--14:45
 * @describe:
 */
public class TableUtils {

    public static String dorisName(String datasetId) {
        return "ds_" + datasetId.replace("-", "_");
    }

    public static String dorisTmpName(String dorisName) {
        return "tmp_" + dorisName;
    }

    public static String dorisDeleteName(String dorisName) {
        return "delete_" + dorisName;
    }

    public static String dorisAddName(String dorisName) {
        return "add_" + dorisName;
    }

    public static String dorisFieldName(String dorisName) {
        return "f_" + Md5Utils.md5(dorisName);
    }

    public static String dorisFieldNameShort(String dorisName) {
        return "f_" + Md5Utils.md5(dorisName).substring(8, 24);
    }

    public static String columnName(String filedName) {
        return "C_" + Md5Utils.md5(filedName);
    }
}
