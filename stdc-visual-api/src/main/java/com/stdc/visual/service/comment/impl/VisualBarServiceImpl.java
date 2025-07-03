package com.stdc.visual.service.comment.impl;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.dynamic.base.dataset.dto.component.SortConditionType;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.ExtSeries;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.Series;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualBar;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualLineBar;
import com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue.VisualKeyValue;
import com.stdc.visual.dynamic.base.dataset.dto.condition.SortCondition;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.service.comment.IVisualBarService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/25--15:37
 * @describe:
 */
@Service
public class VisualBarServiceImpl implements IVisualBarService {

    @Autowired
    private ComponentService componentService;

    @Override
    public VisualBar refresh(DataSetTableRequest dataSetTableRequest) throws Exception {
        String dimension = dataSetTableRequest.getDimensionField();
        String measure = dataSetTableRequest.getMeasureField();
//        if (StringUtil.isBlank(dimension) || StringUtil.isBlank(measure)){
//            BaseException.throwException(ResultCode.get("dime_meas_empty"));
//        }
        if(dimension.split(",").length > 1 || measure.split(",").length > 1){
            BaseException.throwException(ResultCode.get("dime_meas_error"));
        }
        VisualBar visualBar = componentService.freshDataForBar(dataSetTableRequest);
        visualBar = sortByBar(visualBar,dataSetTableRequest);
        return visualBar;
    }

    @Override
    public VisualLineBar refreshLineBar(DataSetTableRequest dataSetTableRequest) throws Exception {
        //柱状折线图
        VisualLineBar result = new VisualLineBar();
        List<String> meaS = Arrays.asList(dataSetTableRequest.getMeasureField().split(","));
        if (meaS.size() != 2){
            BaseException.throwException(ResultCode.get("dime_meas_error"));
        }
        //折线图维度
        String lineMea = new String(meaS.get(0));
        //柱状图维度
        String barMea = new String(meaS.get(1));
        dataSetTableRequest.setMeasureField(lineMea);
        VisualBar lineRes = sortByBar(componentService.freshDataForBar(dataSetTableRequest),dataSetTableRequest);
        dataSetTableRequest.setMeasureField(barMea);
        VisualBar barRes = sortByBar(componentService.freshDataForBar(dataSetTableRequest),dataSetTableRequest);
        //设置结果集
        result.setCategories(lineRes.getCategories());
        result.setSeries(new ArrayList<>());
        ArrayList<ExtSeries> lineSeries = new ArrayList<>();
        ArrayList<ExtSeries> barSeries = new ArrayList<>();
        lineRes.getSeries().forEach(line->{
            ExtSeries extSerie = new ExtSeries();
            BeanUtils.copyBean(extSerie,line);
            extSerie.setType("line");
            result.getSeries().add(extSerie);
        });
        barRes.getSeries().forEach(bar->{
            ExtSeries extSerie = new ExtSeries();
            BeanUtils.copyBean(extSerie,bar);
            extSerie.setType("bar");
            result.getSeries().add(extSerie);
        });
        return result;
    }

    /***
     * bar数据格式 排序
     * @param visualBar
     * @param dataSetTableRequest
     */
    public VisualBar sortByBar(VisualBar visualBar, DataSetTableRequest dataSetTableRequest){
        if (ObjectUtil.isEmpty(dataSetTableRequest.getFieldCondition()) || CollectionUtils.isEmpty(dataSetTableRequest.getFieldCondition().getSortConditionS())){
            return visualBar;
        }
        //将图例分类去除，度量默认相加
        List<VisualKeyValue> dtoS = new LinkedList<>();
        int total = visualBar.getCategories().size();
        ArrayList<String> categories = new ArrayList<>(visualBar.getCategories());
        for (int i = 0; i < total; i++) {
            VisualKeyValue v = new VisualKeyValue();
            v.setName(categories.get(i));
            for (Series serie: visualBar.getSeries()) {
                v.setValue(v.getValue() + serie.getData().get(i));
            }
            dtoS.add(v);
        }
        //维度、度量准备好之后，进行排序操作
        AtomicReference<List<VisualKeyValue>> sortS = new AtomicReference<List<VisualKeyValue>>(dtoS);
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
                        //图例
                    }else if (dataSetTableRequest.getLegendField().equals(sortCondition.getFieldId())){
                        List<Series> series = visualBar.getSeries();
                        //根据图例正序排序
                        if (StringUtil.equals(SortConditionType.ASC, sortCondition.getSortType())){
                            visualBar.setSeries(series.stream().sorted(Comparator.comparing(Series::getName)).collect(Collectors.toList()));
                            //根据图例逆序排序
                        }else if (StringUtil.equals(SortConditionType.DESC, sortCondition.getSortType())){
                            visualBar.setSeries(series.stream().sorted(Comparator.comparing(Series::getName).reversed()).collect(Collectors.toList()));
                            //根据图例自定义排序
                        }else if (StringUtil.equals(SortConditionType.CUSTOM, sortCondition.getSortType())){
                            if (!StringUtil.isBlank(sortCondition.getCustomMsg())){
                                //自定义排序
                                List<String> customS = Arrays.asList(sortCondition.getCustomMsg().split(","));
                                //自定义排序的图例位置下标
                                List<Integer> customIndex = new LinkedList<>();
                                for (String custom : customS) {
                                    for (Series ser: visualBar.getSeries()) {
                                        if (StringUtil.equals(custom,ser.getName())){
                                            //将自定义重排序之后的数组小标位置放到中 customIndex
                                            customIndex.add(visualBar.getSeries().indexOf(ser));
                                        }
                                    }
                                }
                                //设置重排序的series
                                List<Series> newSeries = new ArrayList<>(visualBar.getSeries().size());
                                customIndex.forEach(index->{
                                    newSeries.add(visualBar.getSeries().get(index));
                                });
                                visualBar.setSeries(newSeries);
                            }
                        }
                    }
                });
        //定义排序后的VisualBar对象
        VisualBar visualBarSort = new VisualBar(new LinkedHashSet<>(),new ArrayList<>());
        //定义排好序之后，新data中元素在旧data中的数组下标位置
        List<Integer> indexS = new ArrayList<>(total);
        dtoS.forEach(dto->{
            //将旧data中的对应下标位置按照dtos中的维度名称顺序依次放入indexS
            indexS.add(categories.indexOf(dto.getName()));
            //将dtos中排序后的维度名称放入categories
            visualBarSort.getCategories().add(dto.getName());
        });
        //遍历 旧 Series 设置新 Series
        for (Series src : visualBar.getSeries()) {
            Series s = new Series();
            s.setName(src.getName());
            s.setData(new ArrayList<>(total));
            //设置新data中的数据，内容为旧Data中排好序的下标位置
            indexS.forEach(index->{
                s.getData().add(src.getData().get(index));
            });
            visualBarSort.getSeries().add(s);
        }
        //替换旧visualBar
        return visualBarSort;
    }


}
