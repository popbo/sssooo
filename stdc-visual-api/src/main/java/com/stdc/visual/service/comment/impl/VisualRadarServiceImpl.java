package com.stdc.visual.service.comment.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.visual.dynamic.base.dataset.dto.component.rader.VisualRadar;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.service.comment.IVisualRadarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/26--11:41
 * @describe: 雷达图数据格式
 */
@Service
public class VisualRadarServiceImpl implements IVisualRadarService {

    @Autowired
    private DataSetTableService dataSetTableService;

    @Override
    public VisualRadar refreshByRadar(DataSetTableRequest dataSetTableRequest) {
        VisualRadar visualRadar = new VisualRadar();
        JSONObject previewData = null;
        try {
            String str = JSON.toJSONString(dataSetTableService.getPreviewData(dataSetTableRequest, dataSetTableRequest.getPage(), dataSetTableRequest.getPageSize(), null));
            previewData = JSON.parseObject(str);
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //从data中获取预览数据数据字段
        JSONArray previewFieldS = previewData.getJSONArray("fields");
        //从data中获取预览数据
        JSONArray dataS = previewData.getJSONArray("data");
        //雷达图度量
        List<String> legendS= Arrays.asList(dataSetTableRequest.getLegendField().split(","));
        visualRadar.getSeries().add(new VisualRadar.Serie());
        previewFieldS.forEach(field->{
            if (legendS.contains(((JSONObject)field).getString("id"))){
                VisualRadar.DataS data = new VisualRadar.DataS();
                data.setName(((JSONObject)field).getString("name"));
                data.setValue(new ArrayList<>());
                visualRadar.getSeries().get(0).getData().add(data);
            }
        });
        //遍历dataS，筛选 indicator series
        dataS.forEach(v->{
            JSONObject data = (JSONObject) v;
            VisualRadar.Indicator indicator = new VisualRadar.Indicator();
            //筛选出度量字段的dataeaseName，然后获取到当前indicator的name
            indicator.setName(
                    data.getString(
                            ((JSONObject)previewFieldS.stream()
                                    .filter(field ->
                                            StringUtils.equalsIgnoreCase(
                                                    dataSetTableRequest.getDimensionField()
                                                    , ((JSONObject) field).getString("id")))
                                    .collect(Collectors.toList())
                                    .get(0)
                            ).getString("dataeaseName")
                    ));
            //筛选出 度量最大标准 字段的dataeaseName，然后获取到当前indicator的max
            indicator.setMax(Long.valueOf(
                    data.getString(
                            ((JSONObject)previewFieldS.stream()
                                    .filter(field ->
                                            StringUtils.equalsIgnoreCase(
                                                    dataSetTableRequest.getMeasureField()
                                                    , ((JSONObject) field).getString("id")))
                                    .collect(Collectors.toList())
                                    .get(0)
                            ).getString("dataeaseName")
                    )
            ));
            visualRadar.getIndicator().add(indicator);
            //获取到度量标准值的field
            List<Object> legend = previewFieldS.stream()
                    .filter(field -> legendS.contains(((JSONObject) field).getString("id")))
                    .collect(Collectors.toList());
            //获取到图例字段对应的值 和 名称
            legend.forEach(l->{
                String value = data.getString(((JSONObject) l).getString("dataeaseName"));
                String name = ((JSONObject) l).getString("name");
                //相同的名称，就加到对应的list中去
                visualRadar.getSeries().get(0).getData().forEach(serie->{
                    if (StringUtils.equalsIgnoreCase(serie.getName(),name)){
                        serie.getValue().add(Long.valueOf(value));
                    }
                });
            });
        });
        return visualRadar;
    }
}
