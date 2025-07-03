package com.stdc.visual.service.comment.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.visual.dynamic.base.dataset.dto.component.crosstable.TableHead;
import com.stdc.visual.dynamic.base.dataset.dto.component.crosstable.VisualCrossTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.service.comment.IVisualCrossTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/26--16:37
 * @describe: 交叉表
 */
@Service
public class VisualCrossTableService implements IVisualCrossTableService {

    @Autowired
    private DataSetTableService dataSetTableService;

    /**
     * 交叉表不重写了 太麻烦了 第一版本的有一个小bug，但是不影响使用，2022年5月26日16:47:26
     * 就是 后续应该修改成 排除空数据
     * @param dataSetTableRequest
     * @return
     */
    @Override
    public VisualCrossTable refreshByCrossTable(DataSetTableRequest dataSetTableRequest) {
        VisualCrossTable visualCrossTable = new VisualCrossTable();
        //为表格时，查询数据时去除 图例、度量、维度
        String tableDimesion = dataSetTableRequest.getDimensionField();
        String tableMeasure = dataSetTableRequest.getMeasureField();
        String tableLegend = dataSetTableRequest.getLegendField();
        dataSetTableRequest.setDimensionField("");
        dataSetTableRequest.setMeasureField("");
        dataSetTableRequest.setLegendField("");
        JSONObject previewData = null;
        try {
            String str = JSON.toJSONString(dataSetTableService.getPreviewData(dataSetTableRequest, dataSetTableRequest.getPage(), dataSetTableRequest.getPageSize(), null));
            previewData = JSON.parseObject(str);
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //重新赋值
        dataSetTableRequest.setDimensionField(tableDimesion);
        dataSetTableRequest.setMeasureField(tableMeasure);
        dataSetTableRequest.setLegendField(tableLegend);
        //数据格式处理
        visualCrossTable =  refreshByCrossTable(dataSetTableRequest, previewData);
        return visualCrossTable;
    }

    /***
     * 交叉表数据格式
     * @param dataSetTableRequest
     * @param previewData
     * @return
     */
    private VisualCrossTable refreshByCrossTable(DataSetTableRequest dataSetTableRequest,JSONObject previewData){
        //从data中获取预览数据数据字段
        JSONArray previewFieldS = previewData.getJSONArray("fields");
        //从data中获取预览数据
        JSONArray dataS = previewData.getJSONArray("data");
        //交叉表数据格式视图对象
        VisualCrossTable crossTableVO = new VisualCrossTable();
        //初始化字段
        previewFieldS.stream().filter(field-> (dataSetTableRequest.getRowHead()+dataSetTableRequest.getColHead()+dataSetTableRequest.getTarget()).contains(((JSONObject)field).toJavaObject(DatasetTableField.class).getId()))
                .collect(Collectors.toList()).forEach(field -> crossTableVO.getFieldS().add(((JSONObject)field).toJavaObject(DatasetTableField.class)));
        //获取行表头
        List<String> rowIdS = Arrays.asList((dataSetTableRequest.getRowHead() == null?"":dataSetTableRequest.getRowHead()).split(","));
        //获取列表头
        List<String> colIdS = Arrays.asList((dataSetTableRequest.getColHead() == null?"":dataSetTableRequest.getColHead()).split(","));
        //获取列表指标
        List<String> targetIdS = Arrays.asList((dataSetTableRequest.getTarget() == null?"":dataSetTableRequest.getTarget()).split(","));
        //临时缓存，存储对应的field对象
        Map<String, DatasetTableField> rowFieldMap = new LinkedHashMap<>();
        Map<String,DatasetTableField> colFieldMap = new LinkedHashMap<>();
        Map<String,DatasetTableField> targetFieldMap = new LinkedHashMap<>();
        //将行表头匹配为dataeaseName
        List<String> rowDataeaseNameS = rowIdS.stream().map(row -> {
            String dataeaseName = "";
            for (DatasetTableField field : crossTableVO.getFieldS()) {
                if (StringUtils.equals(field.getId(), row)) {
                    dataeaseName = field.getDataeaseName();
                    rowFieldMap.put(dataeaseName,field);
                    break;
                }
            }
            return dataeaseName;
        }).collect(Collectors.toList());
        //将列表头匹配为dataeaseName
        List<String> colDataeaseNameS = colIdS.stream().map(col -> {
            String dataeaseName = "";
            for (DatasetTableField field : crossTableVO.getFieldS()) {
                if (StringUtils.equals(field.getId(), col)) {
                    dataeaseName = field.getDataeaseName();
                    colFieldMap.put(dataeaseName,field);
                    break;
                }
            }
            return dataeaseName;
        }).collect(Collectors.toList());
        //将列表指标匹配为dataeaseName
        List<String> targetDataeaseNameS = targetIdS.stream().map(col -> {
            String dataeaseName = "";
            for (DatasetTableField field : crossTableVO.getFieldS()) {
                if (StringUtils.equals(field.getId(), col)) {
                    dataeaseName = field.getDataeaseName();
                    targetFieldMap.put(dataeaseName,field);
                    break;
                }
            }
            return dataeaseName;
        }).collect(Collectors.toList());
        //遍历表头字段，初始化交叉表行表头
        crossTableVO.setRowHeads(initHeadS(rowDataeaseNameS,dataS,rowFieldMap));
        //遍历表头字段，初始化交叉表列表头
        crossTableVO.setColHeads(initHeadS(colDataeaseNameS,dataS,colFieldMap));
        //获取指标
        List<TableHead> targetHeadS = initTarget(targetDataeaseNameS, targetFieldMap);
        //设置数据
        crossTableVO.setData(initData(crossTableVO,dataS,targetHeadS));
        //设置总数
        crossTableVO.setTotal(crossTableVO.getData().size());
        //列表头最后一层添加指标
        addTargetsForCol(crossTableVO.getColHeads(),targetHeadS);
        //转换VO对象
        return crossTableVO.VO();
    }

    /**
     * 初始化交叉表表头
     * @param dataeaseNameS
     * @param dataS
     * @param fieldMap
     * @return
     */
    private List<TableHead> initHeadS(List<String> dataeaseNameS, JSONArray dataS, Map<String,DatasetTableField> fieldMap ){
        //按顺序设置每一层字段
        List<List<TableHead>> row = new LinkedList<>();
        for (int i = 0; i < dataeaseNameS.size(); i++) {
            row.add(new LinkedList<>());
        }
        for (int i = 0; i < dataeaseNameS.size(); i++) {
            String rowDataeaseName = dataeaseNameS.get(i);
            //获取当前dataeaseName对应的所有去重value
            Set<String> valueS = new HashSet<>();
            for (Object d : dataS) {
                JSONObject data = (JSONObject)d;
                valueS.add(data.getString(rowDataeaseName));
            }
            for (String value : valueS) {
                TableHead initHead = new TableHead();
                initHead.setId(rowDataeaseName);
                initHead.setValue(value);
                initHead.setFieldName(fieldMap.get(rowDataeaseName).getName());
                initHead.setOriginName(fieldMap.get(rowDataeaseName).getOriginName());
                initHead.setFilter(initHead.getId() + "_&_" + initHead.getValue());
                if (i > 0){
                    initHead.setParentId(dataeaseNameS.get(i-1));
                }
                row.get(i).add(initHead);
            }
            row.get(i).sort(Comparator.comparing(TableHead::getValue));
        }
        if (row.size() > 1){
            initHeadsFilter(row);
        }
        //返回表头
        return row.get(0);
    }

    /**
     * 初始化指标
     * @param dataeaseNameS
     * @param fieldMap
     * @return
     */
    private List<TableHead> initTarget(List<String> dataeaseNameS,Map<String,DatasetTableField> fieldMap){
        List<TableHead> target = new ArrayList<>(dataeaseNameS.size());
        dataeaseNameS.forEach(dataeaseName->{
            TableHead initHead = new TableHead();
            initHead.setId(dataeaseName);
            initHead.setValue(fieldMap.get(dataeaseName).getName());
            initHead.setFieldName(fieldMap.get(dataeaseName).getName());
            initHead.setOriginName(fieldMap.get(dataeaseName).getOriginName());
            initHead.setFilter(initHead.getId()+"_&_"+initHead.getValue());
            target.add(initHead);
        });
        return target;
    }

    /** 初始化数据
     * @param crossTableVO
     * @param dataS
     * @param targetHeadS
     */
    private List<Object> initData(VisualCrossTable crossTableVO, JSONArray dataS, List<TableHead> targetHeadS){
        List<Object> res = new ArrayList<>();
        //获取指标筛选条件
        List<String> targetFilterS = targetHeadS.stream().map(tableHead -> tableHead.getFilter()).collect(Collectors.toList());
        //获取行筛选条件
        List<Set<String>> rowFilterS = initFilter(crossTableVO.getRowHeads());
        //获取列筛选条件
        List<Set<String>> colFilterS = initFilter(crossTableVO.getColHeads());
        Map<String,String> filterMap = new LinkedHashMap<>();
        //遍历列条件
        colFilterS.forEach(colFilters->{
            //单行列条件
            colFilters.forEach(filter->{
                String[] s = filter.split("_&_");
                filterMap.put(s[0],s[1]);
            });
            //遍历指标条件
            targetFilterS.forEach(targetFilter->{
                List<Object> resList = new ArrayList<>();
                String targetDataeaseName = targetFilter.split("_&_")[0];
                //遍历行筛选条件
                rowFilterS.forEach(rowFilters->{
                    rowFilters.forEach(filter->{
                        String[] s = filter.split("_&_");
                        filterMap.put(s[0],s[1]);
                    });
                    System.out.println(filterMap);
                    JSONObject resObj = null;
                    //遍历数据集
                    for (Object d : dataS){
                        List<Boolean> isExitS = new ArrayList<>();
                        JSONObject data = (JSONObject) d;
                        for (String dataeaseName : filterMap.keySet()) {
                            String filter = filterMap.get(dataeaseName);
                            String real = data.getString(dataeaseName);
                            boolean equals = StringUtils.equals(filter, real);
                            isExitS.add(equals);
                        }
                        //符合所有要求  ps：有逻辑bug 2022年3月29日11:03:57
                        if (!isExitS.contains(false)){
                            resObj = data;
                            break;
                        }
                    }
                    if (ObjectUtil.isNotEmpty(resObj)){
                        resList.add(resObj.getString(targetDataeaseName));
                    }else {
                        resList.add("");
                    }
                });
                res.add(resList);
            });
        });
        return res;
    }
    /**
     * 递归设置列表头
     * @param colHeads
     * @param target
     */
    private void addTargetsForCol(List<TableHead> colHeads,List<TableHead> target){
        List<TableHead> lastS = new ArrayList<>();
        getLastHead(lastS,colHeads);
        lastS.forEach(last->{
            last.setChild(new LinkedHashSet<>(deepCopyList(target)));
        });
    }

    /**
     * 加载筛选条件
     * @param heads
     */
    private List<Set<String>> initFilter(List<TableHead> heads){
        List<Set<String>> filterS = new ArrayList<>();
        List<TableHead> last = new ArrayList<>();
        getLastHead(last,heads);
        last.forEach(head->{
            Set<String> stringSet = new LinkedHashSet<>();
            for (String str : head.getFilter().split("__&__")) {
                stringSet.add(str);
            }
            filterS.add(stringSet);
        });
        return filterS;
    }
    /**
     * 初始化表头
     * @return
     */
    private void initHeadsFilter(List<List<TableHead>> row){
        //设置上下级关系
        for (int i = 0; i < row.size() - 1; i++) {
            final Integer index = i + 1;
            List<TableHead> parentS = row.get(i);
            List<TableHead> childS = row.get(index);
            parentS.forEach(parent->{
                childS.forEach(child->{
                    parent.getChild().add(child);
                });
            });
        }
        //解决深拷贝问题
        deepInitHeadS(row.get(0));
        //设置下一层表头筛选条件
        initHeadFilter(row.get(0));
        //筛选条件去重
        delRepeat(row.get(0));
    }
    /**
     * 解决深拷贝问题
     */
    private void deepInitHeadS(List<TableHead> heads){
        heads.forEach(head->{
            if (!CollectionUtils.isEmpty(head.getChild())){
                head.setChild(deepCopySet(head.getChild()));
                deepInitHeadS(new ArrayList(head.getChild()));
            }
        });
    }
    /**
     * 设置下一层表头筛选条件
     * @param rootS
     */
    private void initHeadFilter(List<TableHead> rootS){
        for (TableHead root : rootS) {
            LinkedHashSet<TableHead> childS = root.getChild();
            for (TableHead child : childS) {
                if (!child.getFilter().contains(root.getFilter())){
                    child.setFilter(root.getFilter() + "__&__" +child.getFilter());
                }
                if (child.getChild().size() > 0){
                    initHeadFilter(new ArrayList<>(childS));
                }
            }
        }
    }
    //去重
    private void delRepeat(List<TableHead> rootS){
        List<TableHead> lastS = new ArrayList<>();
        getLastHead(lastS,rootS);
        lastS.forEach(last->{
            String delRepeatFilter = String.join("__&__",new LinkedHashSet<>(Arrays.asList(last.getFilter().split("__&__"))));
            last.setFilter(delRepeatFilter);
        });
    }
    /***
     * 获取最后一层表头
     * @param last
     * @param heads
     */
    private void getLastHead(List<TableHead> last,List<TableHead> heads){
        for (TableHead head : heads) {
            if (head.getChild().size() > 0){
                List<TableHead> childS = new LinkedList<>();
                childS.addAll(head.getChild());
                getLastHead(last,childS);
            }else {
                last.add(head);
            }
        }
    }

    /**List深拷贝*/
    public static <T> LinkedHashSet<T> deepCopySet(LinkedHashSet<T> src) {
        List<T> dest = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(new ArrayList<>(src));
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedHashSet<>(dest);
    }

    /**List深拷贝*/
    public static <T> List<T> deepCopyList(List<T> src) {
        List<T> dest = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dest;
    }

}
