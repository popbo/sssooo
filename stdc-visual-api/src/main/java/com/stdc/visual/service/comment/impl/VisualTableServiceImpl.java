package com.stdc.visual.service.comment.impl;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.dynamic.base.dataset.dto.component.SortConditionType;
import com.stdc.visual.dynamic.base.dataset.dto.component.table.VisualTable;
import com.stdc.visual.dynamic.base.dataset.dto.condition.FieldCondition;
import com.stdc.visual.dynamic.base.dataset.dto.condition.SortCondition;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.base.dataset.request.TableHeadRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.dynamic.service.impl.ComponentServiceImpl;
import com.stdc.visual.service.comment.IVisualTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/5/26--13:36
 * @describe:
 */
@Service
public class VisualTableServiceImpl implements IVisualTableService {

    @Autowired
    private ComponentService componentService;

    @Override
    public VisualTable refreshByTable(DataSetTableRequest dataSetTableRequest) throws Exception {
        VisualTable visualTable = componentService.refreshByTable(dataSetTableRequest);
        sortByTable(dataSetTableRequest,visualTable);
        return visualTable;
    }

    @Override
    public VisualTable refreshByTableWithTableHead(DataSetTableRequest dataSetTableRequest) throws Exception {
        VisualTable visualTable = null;
        if (CollectionUtils.isEmpty(dataSetTableRequest.getTableHead())){
            visualTable = componentService.refreshByTable(dataSetTableRequest);
            sortByTable(dataSetTableRequest,visualTable);
        }else {
            //如果一级表头没有 children 报错 主维度未绑定子维度
            for (TableHeadRequest tableHead : dataSetTableRequest.getTableHead()) {
                if (CollectionUtils.isEmpty(tableHead.getChild())) {
                    BaseException.throwException("主维度未绑定子维度");
                }
            }
            visualTable = componentService.refreshByTableWithTableHead(ObjectUtil.deepCopy(dataSetTableRequest));
            sortByTableWithHead(dataSetTableRequest,visualTable);
        }
        return visualTable;
    }

    /**
     * 排序
     * @param dataSetTableRequest
     * @param visualTable
     * @return
     */
    private void sortByTable(DataSetTableRequest dataSetTableRequest,VisualTable visualTable){
        //字段条件-v0.0.1版本只有排序条件
        FieldCondition fieldCondition = dataSetTableRequest.getFieldCondition();
        if (ObjectUtil.isNotEmpty(fieldCondition) && !CollectionUtils.isEmpty(fieldCondition.getSortConditionS())){
            //筛选排序字段
            fieldCondition.getSortConditionS().stream()
                    .filter(sortCondition -> StringUtil.isNotBlank(sortCondition.getFieldId()))
                    //按照优先级排序，保证优先级最高的字段在最后执行
                    .sorted(Comparator.comparing(SortCondition::getPriority))
                    .forEach(sortCondition->{
                        String fieldId = sortCondition.getFieldId();
                        List<Map<String, String>> dataS = visualTable.getData();
                        //度量字段排序(字符串)
                        if (dataSetTableRequest.getDimensionField().contains(fieldId)){
                            switch (sortCondition.getSortType()){
                                //正序排序
                                case SortConditionType.ASC:
                                    dataS.sort(Comparator.comparing(data->data.get(fieldId)));
                                    break;
                                //逆序排序
                                case SortConditionType.DESC:
                                    dataS.sort(Comparator.comparing(data->((Map<String, String>)data).get(fieldId)).reversed());
                                    break;
                                default:
                                    break;
                            }
                        //维度字段排序(数值)
                        }else if(dataSetTableRequest.getMeasureField().contains(fieldId)){
                            switch (sortCondition.getSortType()){
                                //正序排序
                                case SortConditionType.ASC:
                                    dataS.sort(Comparator.comparing(data-> Double.valueOf(data.get(fieldId))));
                                    break;
                                //逆序排序
                                case SortConditionType.DESC:
                                    dataS.sort(Comparator.comparing(data->Double.valueOf(((Map<String, String>)data).get(fieldId))).reversed());
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    /**
     * 排序 -- > 带有表头情况
     * @param dataSetTableRequest
     * @param visualTable
     * @return
     */
    private void sortByTableWithHead(DataSetTableRequest dataSetTableRequest,VisualTable visualTable){
        //字段条件-v0.0.1版本只有排序条件
        FieldCondition fieldCondition = dataSetTableRequest.getFieldCondition();
        if (ObjectUtil.isNotEmpty(fieldCondition)
                && !CollectionUtils.isEmpty(fieldCondition.getSortConditionS())){
            //筛选排序字段
            fieldCondition.getSortConditionS().stream()
                    .filter(sortCondition -> StringUtil.isNotBlank(sortCondition.getFieldId()))
                    //去除表头字段
                    .filter(sortCondition -> !ComponentServiceImpl.getTableHeadS(dataSetTableRequest.getTableHead()).contains(sortCondition.getFieldId()))
                    //按照优先级排序，保证优先级最高的字段在最后执行
                    .sorted(Comparator.comparing(SortCondition::getPriority))
                    .forEach(sortCondition->{
                        String fieldId = sortCondition.getFieldId();
                        List<Map<String, String>> dataS = visualTable.getData();
                        //度量字段排序(字符串)
                        if ((StringUtil.hasText(dataSetTableRequest.getDimensionField())? dataSetTableRequest.getDimensionField() : "").contains(fieldId)){
                            switch (sortCondition.getSortType()){
                                //正序排序
                                case SortConditionType.ASC:
                                    dataS.sort(Comparator.comparing(data->data.get(fieldId)));
                                    break;
                                //逆序排序
                                case SortConditionType.DESC:
                                    dataS.sort(Comparator.comparing(data->((Map<String, String>)data).get(fieldId)).reversed());
                                    break;
                                default:
                                    break;
                            }
                            //维度字段排序(数值)
                        }else if((StringUtil.hasText(dataSetTableRequest.getMeasureField())?dataSetTableRequest.getMeasureField():"").contains(fieldId)){
                            switch (sortCondition.getSortType()){
                                //正序排序
                                case SortConditionType.ASC:
                                    dataS.sort(Comparator.comparing(data-> Double.valueOf(data.get(fieldId))));
                                    break;
                                //逆序排序
                                case SortConditionType.DESC:
                                    dataS.sort(Comparator.comparing(data->Double.valueOf(((Map<String, String>)data).get(fieldId))).reversed());
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }
}
