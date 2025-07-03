package com.stdc.visual.service.comment.impl;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.dynamic.base.dataset.dto.component.SortConditionType;
import com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue.VisualKeyValue;
import com.stdc.visual.dynamic.base.dataset.dto.condition.SortCondition;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.service.comment.IVisualKeyValueService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/23--16:55
 * @describe:
 */
@Service
public class VisualKeyValueServiceImpl implements IVisualKeyValueService {

    @Autowired
    private ComponentService componentService;

    @Override
    public List<VisualKeyValue> refresh(DataSetTableRequest dataSetTableRequest) throws Exception {
        String dimension = dataSetTableRequest.getDimensionField();
        String measure = dataSetTableRequest.getMeasureField();
        if (StringUtil.isBlank(dimension) || StringUtil.isBlank(measure)){
            BaseException.throwException(ResultCode.get("dime_meas_empty"));
        }
        if(dimension.split(",").length > 1 || measure.split(",").length > 1){
            BaseException.throwException(ResultCode.get("dime_meas_error"));
        }
        List<VisualKeyValue> keyValueList = componentService.freshDataForKeyValue(dataSetTableRequest);
        keyValueList = sortByKeyValue(keyValueList,dataSetTableRequest);
        return keyValueList;
    }

    /**
     * key-value格式数据排序
     * @return
     */
    public List<VisualKeyValue> sortByKeyValue(List<VisualKeyValue> res, DataSetTableRequest dataSetTableRequest){
        if (ObjectUtil.isEmpty(dataSetTableRequest.getFieldCondition()) || CollectionUtils.isEmpty(dataSetTableRequest.getFieldCondition().getSortConditionS())){
            return res;
        }
        AtomicReference<List<VisualKeyValue>> sortS = new AtomicReference<List<VisualKeyValue>>(res);
        //排序条件
        List<SortCondition> sortConditionS = dataSetTableRequest.getFieldCondition().getSortConditionS();
        //按照优先级排序，保证优先级最高的字段在最后执行
        sortConditionS.stream()
                .sorted(Comparator.comparing(SortCondition::getPriority))
                .collect(Collectors.toList())
                //按照排好序的条件，将优先级最低的条件先进行排序
                .forEach(sortCondition -> {
                    //维度
                    if (dataSetTableRequest.getDimensionField().equals(sortCondition.getFieldId())){
                        //根据维度正序排序
                        if (StringUtil.equals(SortConditionType.ASC, sortCondition.getSortType())){
                            sortS.get().sort(Comparator.comparing(VisualKeyValue::getName));
                            //根据维度逆序排序
                        }else if (StringUtil.equals(SortConditionType.DESC, sortCondition.getSortType())){
                            sortS.get().sort(Comparator.comparing(VisualKeyValue::getName).reversed());
                        }
                        //度量
                    } else if (dataSetTableRequest.getMeasureField().equals(sortCondition.getFieldId())){
                        //根据度量正序排序
                        if (StringUtil.equals(SortConditionType.ASC, sortCondition.getSortType())){
                            sortS.get().sort(Comparator.comparing(VisualKeyValue::getValue));
                            //根据度量逆序排序
                        }else if (StringUtil.equals(SortConditionType.DESC, sortCondition.getSortType())){
                            sortS.get().sort(Comparator.comparing(VisualKeyValue::getValue).reversed());
                        }
                    }
                });
        return sortS.get();
    }

}
