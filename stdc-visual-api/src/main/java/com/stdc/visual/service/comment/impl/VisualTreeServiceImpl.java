package com.stdc.visual.service.comment.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.dynamic.base.dataset.dto.component.tree.VisualTree;
import com.stdc.visual.dynamic.base.dataset.dto.component.tree.VisualTreeUtil;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.service.comment.IVisualTreeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:58
 * @describe: 层级树刷新
 */
@Service
public class VisualTreeServiceImpl implements IVisualTreeService {

    @Autowired
    private DataSetTableService dataSetTableService;

    @Override
    public Set<VisualTree> refreshByTree(DataSetTableRequest dataSetTableRequest) throws Exception {
        //查询数据时去除 维度 和 度量
        String dimensionField = new String(dataSetTableRequest.getDimensionField());
        String measureField = new String(dataSetTableRequest.getMeasureField());
        dataSetTableRequest.setDimensionField("");
        dataSetTableRequest.setMeasureField("");
        JSONObject previewData = null;
        try {
            String str = JSON.toJSONString(dataSetTableService.getPreviewData(dataSetTableRequest, dataSetTableRequest.getPage(), dataSetTableRequest.getPageSize(), null));
            previewData = JSON.parseObject(str);
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        dataSetTableRequest.setDimensionField(dimensionField);
        dataSetTableRequest.setMeasureField(measureField);
        return refreshByTree(dataSetTableRequest,previewData);
    }

    /**
     * 层级树数据格式
     * @param dataSetTableRequest
     * @return
     */
    private Set<VisualTree> refreshByTree(DataSetTableRequest dataSetTableRequest,JSONObject previewData) {
        //从data中获取预览数据数据字段
        JSONArray previewFieldS = previewData.getJSONArray("fields");
        //从data中获取预览数据
        JSONArray dataS = previewData.getJSONArray("data");
        //维度（展示值）
        List<String> dimeS = Arrays.asList(dataSetTableRequest.getDimensionField().split(","));
        //维度id转换为dataeaseName
        List<String> dimeDataeaseNameS = dimeS.stream().map(dime -> {
            for (Object obj : previewFieldS) {
                JSONObject o = (JSONObject) obj;
                if (StringUtil.equals(o.getString("id"), dime)) {
                    return o.getString("dataeaseName");
                }
            }
            return null;
        }).collect(Collectors.toList());
        //度量（value值） 不为数组
        String measureField = dataSetTableRequest.getMeasureField();
        //度量id转换为dataeaseName
        String meaDataeaseName = null;
        for (Object obj : previewFieldS) {
            JSONObject o = (JSONObject) obj;
            if (StringUtil.equals(o.getString("id"), measureField)) {
                meaDataeaseName = o.getString("dataeaseName");
            }
        }
        return initTree(dimeDataeaseNameS, meaDataeaseName,dataS);
    }

    /**
     * 初始化树结构
     * @param dimeDataeaseNameS
     * @param meaDataeaseName
     * @param dataS
     * @return
     */
    private Set<VisualTree> initTree(List<String> dimeDataeaseNameS,String meaDataeaseName,JSONArray dataS){
        //按顺序设置每一层字段
        List<Set<VisualTree>> treeS = new LinkedList<>();
        for (int i = 0; i < dimeDataeaseNameS.size(); i++) {
            treeS.add(new LinkedHashSet<>());
        }
        for (int i = 0; i < dimeDataeaseNameS.size(); i++) {
            String dataeaseName = dimeDataeaseNameS.get(i);
            //获取当前dataeaseName对应的所有去重value
            Set<String> valueS = new HashSet<>();
            for (Object d : dataS) {
                JSONObject data = (JSONObject)d;
                valueS.add(data.getString(dataeaseName));
            }
            for (String value : valueS) {
                VisualTree tree = new VisualTree();
                tree.setId(dataeaseName);
                tree.setLabel(value);
                treeS.get(i).add(tree);
            }
        }
        Map<String, VisualTree> rootMap = VisualTreeUtil.initRootTree(treeS, dataS);
        Set<VisualTree> rootS = new LinkedHashSet<>();
        rootMap.keySet().forEach(k->rootS.add(rootMap.get(k)));
        //初始化value值
        initValue(rootS,meaDataeaseName,new HashMap(),dataS);
        //返回第一层
        return rootS;
    }

    /**
     * 初始化value值
     * @param rootS
     * @param meaDataeaseName
     * @param labelCache
     * @param dataS
     */
    private void initValue(Set<VisualTree> rootS,String meaDataeaseName,Map<String,String> labelCache,JSONArray dataS){
        for (VisualTree root : rootS) {
            if (CollectionUtils.isEmpty(root.getChildren())){
                labelCache.put(root.getId(),root.getLabel());
                for (Object d : dataS) {
                    boolean isValue = true;
                    JSONObject data = (JSONObject)d;
                    for (String dataeaseName : data.keySet()) {
                        for (String cacheDataeaseName : labelCache.keySet()) {
                            if (StringUtil.equals(dataeaseName,cacheDataeaseName)){
                                isValue = isValue && StringUtil.equals(data.getString(dataeaseName),labelCache.get(cacheDataeaseName));
                            }
                        }
                    }
                    if (isValue) {
                        root.setValue(data.getString(meaDataeaseName));
                        break;
                    }
                }
            }
            labelCache.put(root.getId(),root.getLabel());
            initValue(root.getChildren(),meaDataeaseName,labelCache,dataS);
        }
    }

}
