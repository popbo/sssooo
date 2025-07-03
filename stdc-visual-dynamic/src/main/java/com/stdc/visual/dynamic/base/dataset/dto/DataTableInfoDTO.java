package com.stdc.visual.dynamic.base.dataset.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:27
 * @describe:
 */
@Data
public class DataTableInfoDTO {
    private String table;
    private String sql;
    private List<ExcelSheetData> excelSheetDataList;
    private String data;// file path
    private List<DataTableInfoCustomUnion> list;// 自定义数据集
    private List<UnionDTO> union;// 关联数据集
}
