package com.stdc.visual.dynamic.base.dataset.request;

import com.stdc.core.tool.utils.StringPool;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.dto.ApiDefinition;
import com.stdc.visual.dynamic.base.dataset.dto.ExcelSheetData;
import com.stdc.visual.dynamic.base.dataset.dto.LinkCustomFilter;
import com.stdc.visual.dynamic.base.dataset.dto.condition.FieldCondition;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.datasource.dto.TableFiled;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/16--20:28
 * @describe:
 */
@Data
@ApiModel(description = "组件刷新请求")
public class DataSetTableRequest extends DatasetTable {
    @ApiModelProperty("排序")
    private String sort;
    @ApiModelProperty("表名集合")
    private List<String> tableNames;
    @ApiModelProperty("行数")
    private String row = "1000";
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("同步类型")
    private String syncType;
    @ApiModelProperty("编辑类型")
    private Integer editType;
    @ApiModelProperty("是否重命名")
    private Boolean isRename;
    @ApiModelProperty("类型过滤条件集合")
    private List<String> typeFilter;
    @ApiModelProperty("字段集合")
    private List<TableFiled> fields;
    @ApiModelProperty("excel sheet集合")
    private List<ExcelSheetData> sheets;
    @ApiModelProperty("是否合并sheet")
    private boolean mergeSheet = false;
    @ApiModelProperty("维度/雷达图显示维度")
    private String dimensionField = "";
    @ApiModelProperty("行表头/交叉表使用 多选使用','号分开")
    private String rowHead;
    @ApiModelProperty("列表头/交叉表使用 多选使用','号分开")
    private String colHead;
    @ApiModelProperty("列表指标/交叉表使用 多选使用','号分开")
    private String target;
    @ApiModelProperty("表格表头数据参数")
    private List<TableHeadRequest> tableHead;
    @ApiModelProperty("度量/雷达图最大度量标准")
    private String measureField = "";
    @ApiModelProperty("图例")
    private String legendField = "";
    @ApiModelProperty("字段条件")
    private FieldCondition fieldCondition;
    @ApiModelProperty("分页页码")
    private Integer page = 1;
    @ApiModelProperty("分页页数")
    private Integer pageSize = Integer.MAX_VALUE;
    @ApiModelProperty("api数据源查询条件")
    private ApiDefinition apiDefinition;
    @ApiModelProperty("where查询条件")
    private List<ChartFieldCustomFilterDTO> whereCustomFilterS;
    @ApiModelProperty("联动查询条件")
    private LinkCustomFilter linkCustomFilterS;
    /**
     * 度量是否默认为sum()相加  是：true  否：false
     */
    private boolean meaSum = true;

    //"度量、维度、图例 id集合,用逗号分隔"
    public String getFieldIDS() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.hasText(this.measureField)){
            stringBuilder.append(this.measureField + ",");
        }
        if (StringUtils.hasText(this.dimensionField)){
            stringBuilder.append(this.dimensionField + ",");
        }
        if (StringUtils.hasText(this.legendField)){
            stringBuilder.append(this.legendField + ",");
        }
        return  stringBuilder.toString();
    }
    //"度量、维度、图例 id集合
    public List<String> getFieldIdList(){
        String fieldIdS = getFieldIDS();
        if (!StringUtils.hasText(fieldIdS)) return null;
        return Arrays.asList(fieldIdS.split(","));
    }

}