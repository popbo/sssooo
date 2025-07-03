package com.stdc.visual.dynamic.service.impl;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionConstants;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.common.utils.TableUtils;
import com.stdc.visual.dynamic.base.chart.dto.ChartCustomFilterItemDTO;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableUnionDTO;
import com.stdc.visual.dynamic.base.dataset.dto.DataTableInfoCustomUnion;
import com.stdc.visual.dynamic.base.dataset.dto.DataTableInfoDTO;
import com.stdc.visual.dynamic.base.dataset.dto.DataView;
import com.stdc.visual.dynamic.base.dataset.dto.component.SortConditionType;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.Series;
import com.stdc.visual.dynamic.base.dataset.dto.component.bar.VisualBar;
import com.stdc.visual.dynamic.base.dataset.dto.component.event.VisualEvent;
import com.stdc.visual.dynamic.base.dataset.dto.component.keyvalue.VisualKeyValue;
import com.stdc.visual.dynamic.base.dataset.dto.component.scatter.VisualScatter;
import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.dynamic.base.dataset.dto.component.table.VisualTable;
import com.stdc.visual.dynamic.base.dataset.dto.component.value.VisualValue;
import com.stdc.visual.dynamic.base.dataset.dto.condition.FieldCondition;
import com.stdc.visual.dynamic.base.dataset.dto.condition.SortCondition;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.base.dataset.request.TableHeadRequest;
import com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.base.datasource.request.DatasourceRequest;
import com.stdc.visual.dynamic.mapper.DatasetTableMapper;
import com.stdc.visual.dynamic.mapper.DatasourceMapper;
import com.stdc.visual.dynamic.provider.ProviderFactory;
import com.stdc.visual.dynamic.provider.datasource.DatasourceProvider;
import com.stdc.visual.dynamic.provider.query.QueryProvider;
import com.stdc.visual.dynamic.service.ComponentService;
import com.stdc.visual.dynamic.service.DataSetTableFieldsService;
import com.stdc.visual.dynamic.service.DataSetTableUnionService;
import com.stdc.visual.dynamic.service.PermissionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.stdc.core.tool.utils.ObjectUtil.deepCopy;

/**
 * @author: wang_jie
 * @data: 2022/5/26--11:04
 * @describe: 组件刷新
 */
@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private DatasetTableMapper datasetTableMapper;

    @Resource
    private DataSetTableFieldsService dataSetTableFieldsService;

    @Resource
    private DatasourceMapper datasourceMapper;

    @Resource
    private DataSetTableUnionService dataSetTableUnionService;

    @Resource
    private PermissionService permissionService;


    @Override
    public List<VisualKeyValue> freshDataForKeyValue(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualKeyValue> keyValueList = new ArrayList<>();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //封装为 key-value List
        viewDTOS.forEach(DataViews -> {
            VisualKeyValue keyValue = new VisualKeyValue();
            DataView key= DataViews.stream().filter(DataView -> StringUtil.equals(dataSetTableRequest.getDimensionField(), DataView.getId())).findFirst().get();
            DataView value= DataViews.stream().filter(DataView -> StringUtil.equals(dataSetTableRequest.getMeasureField(), DataView.getId())).findFirst().get();
            keyValue.setName(key.get());
            keyValue.setValue(Double.valueOf(value.get()));
            keyValueList.add(keyValue);
        });
        return keyValueList;
    }

    @Override
    public VisualBar freshDataForBar(DataSetTableRequest dataSetTableRequest) throws Exception {
        VisualBar visualBar = VisualBar.instance();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //获取分类、获取图例集合
        Set<String> legendTypes = new LinkedHashSet<>();
        Set<String> categories = new LinkedHashSet<>();
        viewDTOS.forEach(dataViews->
                dataViews.forEach(dataView -> {
                    if (StringUtil.hasText(dataSetTableRequest.getLegendField()) && StringUtil.equals(dataView.getId(),dataSetTableRequest.getLegendField())){
                        legendTypes.add(dataView.getValue());
                    }
                    if (StringUtil.equals(dataView.getId(),dataSetTableRequest.getDimensionField())){
                        categories.add(dataView.getValue());
                    }
                })
        );
        //封装为 VisualBar
        //添加维度
        visualBar.setCategories(categories);
        //添加图例 没有图例分类设置默认default
        if (CollectionUtils.isNotEmpty(legendTypes)){
            legendTypes.forEach(legendType->{
                Series series = new Series();
                series.setName(legendType);
                visualBar.getSeries().add(series);
            });
        }else {
            Series series = new Series();
            series.setName("");
            visualBar.getSeries().add(series);
        }
        //添加维度数据
        visualBar.getSeries().forEach(series -> {
            List<Double> data = new LinkedList<>();
            //根据分类顺序添加
            categories.forEach(c->{
                Double value = new Double(0D);
                for (List<DataView> dataViews : viewDTOS) {
                    //维度值
                    String cateValue= dataViews.stream().filter(dataView -> StringUtil.equals(dataView.getId(), dataSetTableRequest.getDimensionField())).findFirst().get().getValue();
                    //度量值
                    String dataValue = dataViews.stream().filter(dataView -> StringUtil.equals(dataView.getId(), dataSetTableRequest.getMeasureField())).findFirst().get().getValue();
                    dataValue =  dataValue==null  ? "0" : dataValue;
                    //有图例存在
                    if (CollectionUtils.isNotEmpty(legendTypes)){
                        //图例值
                        String legendValue = dataViews.stream().filter(dataView -> StringUtil.equals(dataView.getId(), dataSetTableRequest.getLegendField())).findFirst().get().getValue();
                        if (StringUtil.equals(cateValue,c) && StringUtil.equals(legendValue,series.getName())){
                            value = Double.valueOf(dataValue);
                            break;
                        }
                    }else { //默认图例
                        if (StringUtil.equals(cateValue,c)){
                            value = Double.valueOf(dataValue);
                            break;
                        }
                    }
                }
                data.add(value);
            });
            series.setData(data);
        });
        return visualBar;
    }

    @Override
    public List<VisualValue> freshDataForValueByMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualValue> valueList = new ArrayList<>();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //封装为 value List
        viewDTOS.forEach(DataViews -> {
            VisualValue visualValue = new VisualValue();
            DataView value= DataViews.stream().filter(DataView -> StringUtil.equals(dataSetTableRequest.getMeasureField(), DataView.getId())).findFirst().get();
            visualValue.setValue(value.get());
            valueList.add(visualValue);
        });
        return valueList;
    }

    @Override
    public List<VisualValue> freshDataForValueByDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualValue> valueList = new ArrayList<>();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //封装为 value List
        viewDTOS.forEach(DataViews -> {
            VisualValue visualValue = new VisualValue();
            DataView value= DataViews.stream().filter(DataView -> StringUtil.equals(dataSetTableRequest.getDimensionField(), DataView.getId())).findFirst().get();
            visualValue.setValue(value.get());
            valueList.add(visualValue);
        });
        return valueList;
    }

    @Override
    public List<VisualValue> freshDataForValueByMeasureFieldAndDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualValue> valueList = new ArrayList<>();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //维度和度量
        String field = dataSetTableRequest.getDimensionField() + StringPool.COMMA + dataSetTableRequest.getMeasureField();
        //封装为 value List
        viewDTOS.forEach(DataViews -> {
            VisualValue visualValue = new VisualValue();
            DataView value= DataViews.stream().filter(DataView -> field.contains(DataView.getId())).findFirst().get();
            visualValue.setValue(value.get());
            valueList.add(visualValue);
        });
        return valueList;
    }

    @Override
    public VisualTable refreshByTable(DataSetTableRequest dataSetTableRequest) throws Exception {
        VisualTable visualTable = new VisualTable();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        viewDTOS.forEach(dataViews -> {
            Map<String,String> map = new LinkedHashMap<>();
            dataViews.forEach(dataView -> {
                String id = dataView.getId();
                String value = dataView.getValue();
                map.put(id,value);
            });
            visualTable.getData().add(map);
        });
        if (CollectionUtils.isNotEmpty(visualTable.getData())){
            //当前数据集所有列字段
            List<DatasetTableField> fields = dataSetTableFieldsService.getFieldsByTableId(dataSetTableRequest.getId());
            Optional<Map<String, String>> first = visualTable.getData().stream().findFirst();
            if (!first.isPresent()){
                return null;
            }
            Map<String, String> map = first.get();
            Set<String> fieldIdS = map.keySet();
            List<DatasetTableField> fieldsCopy = new ArrayList<>();
            //字段根据fieldIdList顺序排序
            for (String id : fieldIdS) {
                Optional<DatasetTableField> fieldFirst = fields.stream().filter(f -> StringUtil.equals(f.getId(), id)).findFirst();
                if (fieldFirst.isPresent()) fieldsCopy.add(fieldFirst.get());
            }
            fields = fieldsCopy;
            //筛选只要当前data数据中的列
            fields = fields.stream().filter(f -> fieldIdS.contains(f.getId())).collect(Collectors.toList());
            //遍历fields 设置表格column对象
            fields.forEach(f->{
                String id = f.getId();
                String name = f.getName();
                visualTable.getColumn().add(VisualTable.Column.builder()
                                .label(name)
                                .prop(id)
                                .build());
            });
        }
        visualTable.setTotal(visualTable.getData().size());
        return visualTable;
    }

    @Override
    public VisualTable refreshByTableWithTableHead(DataSetTableRequest dataSetTableRequest) throws Exception {
        //取消度量sum相加
        dataSetTableRequest.setMeaSum(false);
        List<TableHeadRequest> tableHeadS = dataSetTableRequest.getTableHead();
        VisualTable visualTable = new VisualTable();
        //查询总数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //查询不含表头的数据 数据会自动去重
        List<String> headS = getTableHeadS(tableHeadS);
        List<String> dimensionFieldList = Arrays.asList(dataSetTableRequest.getDimensionField().split(","));
        List<String> measureFieldList = Arrays.asList(dataSetTableRequest.getMeasureField().split(","));
        for (String head : headS) {
            //删除表头数据列
            dimensionFieldList = dimensionFieldList.stream().filter(v->!StringUtil.equals(head,v)).collect(Collectors.toList());
            measureFieldList = measureFieldList.stream().filter(v->!StringUtil.equals(head,v)).collect(Collectors.toList());
        }
        dataSetTableRequest.setDimensionField(String.join(",",dimensionFieldList));
        dataSetTableRequest.setMeasureField(String.join(",",measureFieldList));
        //得到不含表头列的数据
        List<List<DataView>> resultViewDTOS = queryData(dataSetTableRequest);
        for (TableHeadRequest tableHead : tableHeadS) {
            //查询只含一级表头的数据 数据会自动去重
            dataSetTableRequest.setDimensionField(tableHead.getHead());
            dataSetTableRequest.setMeasureField(null);
            //得到只含一级表头列的数据
            //开启度量sum相加,group去重
            dataSetTableRequest.setMeaSum(true);
            List<List<DataView>> level1HeadViewDTOS = queryData(dataSetTableRequest);
            List<DataView> level1Head = new ArrayList<>();
            level1HeadViewDTOS.forEach(level1HeadS->{
                level1Head.add(level1HeadS.get(0));
            });
            //得出新列  一级分类 + 二级分类
            List<DataView> newColumnS = new ArrayList<>();
            level1Head.forEach(level1->{
                tableHead.getChild().forEach(c->{
                    //新列id
                    newColumnS.add(DataView.builder()
                            .id(String.join(",", level1.getId(), c.getHead()))
                            .dataeaseName(String.join(",", level1.getValue(), c.getHead()))
                            .value(null).build());
                });
            });
            //处理表头数据
            for (List<DataView> resultDto : resultViewDTOS) {
                List<List<DataView>> wantViewDTO = new ArrayList<>();
                for (List<DataView> viewDTO : viewDTOS) {
                    Boolean isWant = true;
                    for (DataView resultView : resultDto) {
                        Boolean check = false;
                        for (String head : headS) {
                            check = check || resultView.getId().contains(head);
                        }
                        if (!check){
                            //验证不是表头的数据
                            isWant  = isWant && viewDTO.stream()
                                    .filter(v->StringUtil.equals(v.getId(),resultView.getId()))
                                    .filter(v->StringUtil.equals(v.getValue(),resultView.getValue()))
                                    .findFirst().isPresent();
                        }
                    }
                    if (isWant){
                        //找到我想要的源数据
                        wantViewDTO.add(viewDTO);
                    }
                }
                //源数据进行筛选
                List<DataView> newColumnsCopy = deepCopyList(newColumnS);
                for (DataView dataView : newColumnsCopy) {
                    //一级表头
                    String level1HeadId = dataView.getId().split(",")[0];
                    String level1HeadValue = dataView.getDataeaseName().split(",")[0];
                    //二级表头
                    String level2HeadId = dataView.getId().split(",")[1];
                    for (List<DataView> dataViews : wantViewDTO) {
                        Boolean is = false;
                        for (DataView view : dataViews) {
                            is = is || StringUtil.equals(view.getId(),level1HeadId) && StringUtil.equals(view.getValue(),level1HeadValue);
                        }
                        if (is){
                            dataView.setValue(dataViews.stream().filter(v -> StringUtil.equals(v.getId(),level2HeadId)).findFirst().get().getValue());
                            break;
                        }
                    }
                }
                //将新列加入无表头列中
                resultDto.addAll(newColumnsCopy);
            }
        }
        //设置数据
        resultViewDTOS.forEach(dataViews -> {
            Map<String,String> map = new LinkedHashMap<>();
            dataViews.forEach(dataView -> {
                String id = null;
                if (dataView.getDataeaseName().split(",").length == 2){
                    id = dataView.getDataeaseName();
                }else {
                    id = dataView.getId();
                }
                String value = dataView.getValue();
                map.put(id,value);
            });
            visualTable.getData().add(map);
        });
        //设置表头
        if (CollectionUtils.isNotEmpty(visualTable.getData())){
            /*设置表头*/
            //当前数据集所有列字段
            List<DatasetTableField> fields = dataSetTableFieldsService.getFieldsByTableId(dataSetTableRequest.getId());
            Optional<Map<String, String>> first = visualTable.getData().stream().findFirst();
            if (!first.isPresent()){
                return null;
            }
            Map<String, String> map = first.get();
            Set<String> fieldIdS = map.keySet();
            List<DatasetTableField> fieldsSort = new ArrayList<>();
            //字段根据fieldIdList顺序排序
            for (String id : fieldIdS) {
                Optional<DatasetTableField> fieldFirst = fields.stream().filter(f -> id.contains(f.getId())).findFirst();
                if (fieldFirst.isPresent()) fieldsSort.add(deepCopy(fieldFirst.get()));
            }
            //修改新列
            List<String> fieldIdArrayS = new ArrayList<>();
            for (String fieldId : fieldIdS) {
                fieldIdArrayS.add(fieldId);
            }
            for (int i = 0; i < fieldIdArrayS.size(); i++) {
                String fieldId = fieldIdArrayS.get(i);
                if (fieldId.split(",").length > 1){
                    DatasetTableField field = fieldsSort.get(i);
                    field.setId(fieldId);
                    field.setName(String.join(",",fieldId.split(",")[0],field.getName()));
                }
            }
            //遍历fields 设置表格column对象
            fieldsSort.forEach(f->{
                if (f.getId().split(",").length == 1){
                    String id = f.getId();
                    String name = f.getName();
                    visualTable.getColumn().add(VisualTable.Column.builder()
                            .label(name)
                            .prop(id)
                            .build());
                }
            });
            for (TableHeadRequest tableHead : tableHeadS) {
                //查询只含一级表头的数据 数据会自动去重
                dataSetTableRequest.setDimensionField(tableHead.getHead());
                dataSetTableRequest.setMeasureField(null);
                //得到只含表头列的数据
                List<List<DataView>> level1HeadViewDTOS = queryData(dataSetTableRequest);
                List<DataView> level1Head = new ArrayList<>();
                level1HeadViewDTOS.forEach(level1HeadS->{
                    level1Head.add(level1HeadS.get(0));
                });
                //设置表头对象
                List<String> level1HeadIdS = level1Head.stream().map(l -> l.getValue()).collect(Collectors.toList());
                //表头进行排序
                FieldCondition fieldCondition = dataSetTableRequest.getFieldCondition();
                if (ObjectUtil.isNotEmpty(fieldCondition) && !CollectionUtils.isEmpty(fieldCondition.getSortConditionS())){
                    Optional<SortCondition> headSort = fieldCondition.getSortConditionS().stream()
                            .filter(sortCondition -> StringUtil.isNotBlank(sortCondition.getFieldId()))
                            .filter(sortCondition -> StringUtil.equals(tableHead.getHead(), sortCondition.getFieldId()))
                            .findFirst();
                    if (headSort.isPresent()){
                        SortCondition sortCondition = headSort.get();
                        switch (sortCondition.getSortType()){
                            //正序排序
                            case SortConditionType.ASC:
                                level1HeadIdS.sort(Comparator.comparing(v->v));
                                break;
                            //逆序排序
                            case SortConditionType.DESC:
                                level1HeadIdS.sort(Comparator.comparing(v->v.toString()).reversed());
                                break;
                            default:
                                break;
                        }
                    }
                }
                level1HeadIdS.forEach(l->{
                    List<DatasetTableField> leave2HeadS = fieldsSort.stream().filter(f -> f.getId().split(",")[0].equals(l)).collect(Collectors.toList());
                    List<VisualTable.Column> leave2ColumnS = new ArrayList<>();
                    leave2HeadS.forEach(leave2Head->{
                        leave2ColumnS.add(VisualTable.Column.builder().label(leave2Head.getName().split(",")[1]).prop(leave2Head.getId()).build());
                    });
                    visualTable.getColumn().add(
                            VisualTable.Column.builder()
                                    .label(l)
                                    .prop(l)
                                    .children(leave2ColumnS)
                                    .build());
                });
            }
        }
        visualTable.setTotal(visualTable.getData().size());
        return visualTable;
    }

    @Override
    public List<VisualSelect> refreshSelectWithDimensionField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualSelect> visualSelects = new ArrayList<>();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //维度和度量
        String field = dataSetTableRequest.getDimensionField();

        //封装为 VisualSelect List
        viewDTOS.forEach(DataViews -> {
            Optional<DataView> view = DataViews.stream().filter(DataView -> field.contains(DataView.getId())).findFirst();
            if (view.isPresent()){
                VisualSelect visualSelect = new VisualSelect();
                DataView value = view.get();
                visualSelect.setLabel(value.getValue());
                visualSelect.setValue(value.getValue());
                visualSelects.add(visualSelect);
            }
        });
        return visualSelects;
    }

    @Override
    public List<VisualSelect> refreshSelectWithDimensionFieldAndMeasureField(DataSetTableRequest dataSetTableRequest) throws Exception {
        List<VisualSelect> visualSelects = new ArrayList<>();
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        //封装为 VisualSelect List
        viewDTOS.forEach(DataViews -> {
            VisualSelect visualSelect = new VisualSelect();
            Optional<DataView> viewDime = DataViews.stream().filter(DataView -> dataSetTableRequest.getDimensionField().contains(DataView.getId())).findFirst();
            Optional<DataView> viewMea = DataViews.stream().filter(DataView -> dataSetTableRequest.getMeasureField().contains(DataView.getId())).findFirst();
            if (viewDime.isPresent()){
                visualSelect.setLabel(viewDime.get().getValue());
            }
            if (viewMea.isPresent()){
                visualSelect.setValue(viewMea.get().getValue());
            }
            visualSelects.add(visualSelect);
        });
        return visualSelects;
    }

    @Override
    public List<VisualEvent> refreshByEvent(DataSetTableRequest dataSetTableRequest) throws Exception {
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        List<VisualEvent> events = new ArrayList<>(viewDTOS.size());
        viewDTOS.forEach(dtoS->{
            String title = StringPool.EMPTY;
            String detail = StringPool.EMPTY;
            String time = StringPool.EMPTY;
            switch (dtoS.size()){
                case 1:
                    title = dtoS.get(0) != null ? dtoS.get(0).getValue() : StringPool.EMPTY;
                    break;
                case 2:
                    title = dtoS.get(0) != null ? dtoS.get(0).getValue() : StringPool.EMPTY;
                    detail = dtoS.get(1) != null ? dtoS.get(1).getValue() : StringPool.EMPTY;
                    break;
                case 3:
                    title = dtoS.get(0) != null ? dtoS.get(0).getValue() : StringPool.EMPTY;
                    detail = dtoS.get(1) != null ? dtoS.get(1).getValue() : StringPool.EMPTY;
                    time = dtoS.get(2) != null ? dtoS.get(2).getValue() : StringPool.EMPTY;
                    break;
                default:
                    break;
            }
            events.add(new VisualEvent().setTitle(title).setDetail(detail).setTime(time));
        });
        return events;
    }

    @Override
    public List<VisualScatter>  refreshByScatter(DataSetTableRequest dataSetTableRequest) throws Exception {
        String[] measureS = dataSetTableRequest.getMeasureField().split(",");
        if (measureS.length != 2){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //散点图横坐标
        final String xAId = measureS[0];
        //散点图纵坐标
        final String yAId = measureS[1];
        //查询数据
        List<List<DataView>> viewDTOS = queryData(dataSetTableRequest);
        VisualScatter scatter = new VisualScatter();
        scatter.setData(new ArrayList<>());
        viewDTOS.forEach(DataViews->{
            //点位
            List<Double> point = new ArrayList<>();
            //横坐标
            Double xA = 0D;
            //纵坐标
            Double yA = 0D;
            for (DataView dataView : DataViews) {
                //散点图横坐标数据
                if (StringUtil.equals(xAId,dataView.getId())){
                    xA = Double.valueOf(dataView.getValue());
                }
                //散点图纵坐标数据
                else if (StringUtil.equals(yAId,dataView.getId())){
                    yA = Double.valueOf(dataView.getValue());
                }
            }
            point.add(xA);
            point.add(yA);
            scatter.getData().add(point);
        });
        return Arrays.asList(scatter);
    }

    /**
     * 查询数据
     * @param dataSetTableRequest
     * @return
     */
    private List<List<DataView>> queryData(DataSetTableRequest dataSetTableRequest){
        //查询当前数据集下面的所有字段
        DatasetTableField datasetTableField = DatasetTableField.builder().tableId(dataSetTableRequest.getId()).checked(Boolean.TRUE).build();
        List<DatasetTableField> fields = dataSetTableFieldsService.list(datasetTableField, true);
        //获取数据集详情
        DatasetTable datasetTable = datasetTableMapper.selectById(dataSetTableRequest.getId());
        //TODO 进行列权限筛选
        // 脱敏权限
        List<String> desensitizationList = new ArrayList<>();
        fields = permissionService.filterColumnPermissions(fields,desensitizationList,datasetTable.getId());
        //经过行权限筛选后fields为空，当前用户无权限访问，返回空
        if (CollectionUtils.isEmpty(fields)) {
            BaseException.throwException(ResultCode.get("user_does_not_have_permission_to_access_field"));
        }
        //可用的filed ID集合
        List<String> ids = fields.stream().map(DatasetTableField::getId).collect(Collectors.toList());
        //需要使用的field ID集合
        List<String> fieldIdList = dataSetTableRequest.getFieldIdList();
        //判断fieldId是否为空
        if (ObjectUtil.isEmpty(fieldIdList)) BaseException.throwException("field为空");
        //筛选权限
        List<String> noAuthIdS = new ArrayList<>();
        for (String id : fieldIdList) {
            //需要使用的id，不存在于可用id集合中，无权限访问
            if (!ids.contains(id)){
                noAuthIdS.add(id);
            }
        }
        //如果 权限不足，就抛出权限不足异常
        if (noAuthIdS.size() > 0){
            BaseException.throwException(ResultCode.get("insufficient_field_permissions")+String.join(",", noAuthIdS));
        }
        //筛选只查询条件里的维度和度量
        fields = fields.stream().filter(f -> fieldIdList.contains(f.getId())).collect(Collectors.toList());
        //经过维度、度量字段筛选fields为空
        if (CollectionUtils.isEmpty(fields)) {
            BaseException.throwException(ResultCode.get("user_does_not_have_permission_to_access_field"));
        }
        List<DatasetTableField> fieldsCopy = new ArrayList<>();
        //字段根据fieldIdList顺序排序
        for (String id : fieldIdList) {
            Optional<DatasetTableField> fieldOptional = fields.stream().filter(f -> StringUtil.equals(f.getId(), id)).findFirst();
            if (fieldOptional.isPresent()) fieldsCopy.add(fieldOptional.get());
        }
        fields = fieldsCopy;
        //行条件
        List<ChartFieldCustomFilterDTO> customFilter = new ArrayList<>();
        //TODO 进行行权限筛选
        customFilter = permissionService.filterRowPermissions(dataSetTableRequest,fields, datasetTable);
        // 行权限
        //添加前端传来的where查询条件
        List<ChartFieldCustomFilterDTO> filterS = dataSetTableRequest.getWhereCustomFilterS();
        //filter为空 初始化filters
        if (ObjectUtil.isEmpty(filterS)) filterS = new ArrayList<>();
        //初始化联动查询条件
        if (dataSetTableRequest.getLinkCustomFilterS() != null && dataSetTableRequest.getLinkCustomFilterS().getFilter().size() > 0) {
            for (ChartCustomFilterItemDTO filterItemDTO : dataSetTableRequest.getLinkCustomFilterS().getFilter()) {
                if (!StringUtil.hasText(filterItemDTO.getValue())) continue;
                String tag = "联动" + UUID.randomUUID().toString().replaceAll("-", "");
                ChartFieldCustomFilterDTO link = new ChartFieldCustomFilterDTO();
                link.setConditionName(tag);
                link.setConditionId(tag);
                link.setConditionType("zd");
                link.setIsEdit(null);
                link.setLogic("and");
                link.setField(new DatasetTableField());
                link.getField().setId(filterItemDTO.getFieldId());
                link.setFilter(new ArrayList<>());
                link.getFilter().add(filterItemDTO);
                filterS.add(link);
            }
        }
        //补充DatasetTableField信息
        if (CollectionUtils.isNotEmpty(filterS)) {
            //遍历条件
            for (ChartFieldCustomFilterDTO filter : filterS) {
                //通过id主键获取到完整DatasetTableField,并重新赋值DatasetTableField
                if (ObjectUtils.isNotEmpty(filter.getField())) {
                    filter.setField(dataSetTableFieldsService.get(filter.getField().getId()));
                }
            }
            customFilter.addAll(filterS);
        }
        DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(dataSetTableRequest.getInfo(), DataTableInfoDTO.class);
        //查询数据展示
        List<List<DataView>> viewDTOS = new ArrayList<>();
        //数据库数据集
        if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "db")) {
            if (datasetTable.getMode() == 0) {
                //获取数据源配置
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    BaseException.throwException(ResultCode.get("datasource_delete"));
                }
                //判断数据源是否有效
                if (StringUtils.isNotEmpty(ds.getStatus()) && ds.getStatus().equalsIgnoreCase("Error")) {
                    BaseException.throwException(ResultCode.get("invalid_ds"));
                }
                //获取数据源加载工厂
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                //设置数据源
                datasourceRequest.setDatasource(ds);
                //获取表名
                String table = dataTableInfoDTO.getTable();
                //获取sql工厂
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                //设置sql
                datasourceRequest.setQuery(qp.createQuerySQL(dataSetTableRequest,table, fields, false,ds, customFilter));
                try {
                    //获取数据
                    List<String[]> dataS = datasourceProvider.getData(datasourceRequest);
                    for (String[] data : dataS) {
                        List<DataView> views = new ArrayList<>();
                        for (int i = 0; i < data.length; i++) {
                            DatasetTableField field = fields.get(i);
                            String value = data[i];
                            if (desensitizationList.contains(field.getDataeaseName())){
                                value = ColumnPermissionConstants.DesensitizationTemplate;
                            }
                            views.add(DataView.builder()
                                    .id(field.getId())
                                    .dataeaseName(field.getDataeaseName())
                                    .value(value)
                                    .build());
                        }
                        viewDTOS.add(views);
                    }
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }
        }
        //sql数据集
        else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "sql")) {
            if (datasetTable.getMode() == 0) {
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    BaseException.throwException(ResultCode.get("datasource_delete"));
                }
                if (StringUtils.isNotEmpty(ds.getStatus()) && ds.getStatus().equalsIgnoreCase("Error")) {
                    BaseException.throwException(ResultCode.get("invalid_ds"));
                }
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);

                String sql = "("+dataTableInfoDTO.getSql()+")";
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(qp.createQuerySQL(dataSetTableRequest,sql, fields, false,ds, customFilter));
                try {
                    //获取数据
                    List<String[]> dataS = datasourceProvider.getData(datasourceRequest);
                    for (String[] data : dataS) {
                        List<DataView> views = new ArrayList<>();
                        for (int i = 0; i < data.length; i++) {
                            DatasetTableField field = fields.get(i);
                            String value = data[i];
                            if (desensitizationList.contains(field.getDataeaseName())){
                                value = ColumnPermissionConstants.DesensitizationTemplate;
                            }
                            views.add(DataView.builder()
                                    .id(field.getId())
                                    .dataeaseName(field.getDataeaseName())
                                    .value(value)
                                    .build());
                        }
                        viewDTOS.add(views);
                    }
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }
        }
        //自定义数据集
        else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "custom")) {
            if (datasetTable.getMode() == 0) {
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    BaseException.throwException(ResultCode.get("datasource_delete"));
                }
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);

                DataTableInfoDTO dt = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);
                List<DataSetTableUnionDTO> list = dataSetTableUnionService
                        .listByTableId(dt.getList().get(0).getTableId());

                String sql = "";
                try {
                    sql = "("+getCustomSQLDatasource(dt, list, ds)+")";
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(qp.createQuerySQL(dataSetTableRequest,sql, fields, false,ds, customFilter));
                try {
                    //获取数据
                    List<String[]> dataS = datasourceProvider.getData(datasourceRequest);
                    for (String[] data : dataS) {
                        List<DataView> views = new ArrayList<>();
                        for (int i = 0; i < data.length; i++) {
                            DatasetTableField field = fields.get(i);
                            String value = data[i];
                            if (desensitizationList.contains(field.getDataeaseName())){
                                value = ColumnPermissionConstants.DesensitizationTemplate;
                            }
                            views.add(DataView.builder()
                                    .id(field.getId())
                                    .dataeaseName(field.getDataeaseName())
                                    .value(value)
                                    .build());
                        }
                        viewDTOS.add(views);
                    }
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }
        }
        return viewDTOS;
    }

    /**
     * 获取自定义数据集sql
     * @param dataTableInfoDTO
     * @param list
     * @param ds
     * @return
     */
    public String getCustomSQLDatasource(DataTableInfoDTO dataTableInfoDTO, List<DataSetTableUnionDTO> list,
                                         Datasource ds) {
        DatasourceTypes datasourceTypes = DatasourceTypes.valueOf(ds.getType());
        String keyword = datasourceTypes.getKeywordPrefix() + "%s" + datasourceTypes.getKeywordSuffix();
        Map<String, String[]> customInfo = new TreeMap<>();
        for (DataTableInfoCustomUnion ele : dataTableInfoDTO.getList()) {
            DatasetTable datasetTable = datasetTableMapper.selectById(ele.getTableId());
            String table = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class).getTable();
            if (ObjectUtils.isEmpty(datasetTable)) {
                BaseException.throwException(ResultCode.get("custom_ds_delete"));
            }
            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(ele.getCheckedFields());
            if (CollectionUtils.isEmpty(fields)) {
                BaseException.throwException(ResultCode.get("cst_ds_tb_or_field_deleted"));
            }
            String[] array = fields.stream()
                    .map(f -> String.format(keyword, table) + "." + String.format(keyword, f.getOriginName()) + " AS "
                            + TableUtils.dorisFieldName(ele.getTableId() + "_" + f.getOriginName()))
                    .toArray(String[]::new);
            customInfo.put(table, array);
        }
        DataTableInfoCustomUnion first = dataTableInfoDTO.getList().get(0);
        DatasetTable table = datasetTableMapper.selectById(first.getTableId());
        String tableName = JsonUtil.toJavaObj(table.getInfo(), DataTableInfoDTO.class).getTable();
        if (CollectionUtils.isNotEmpty(list)) {
            StringBuilder field = new StringBuilder();
            Iterator<Map.Entry<String, String[]>> iterator = customInfo.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String[]> next = iterator.next();
                field.append(StringUtils.join(next.getValue(), ",")).append(",");
            }
            String f = field.substring(0, field.length() - 1);

            StringBuilder join = new StringBuilder();
            for (DataTableInfoCustomUnion dataTableInfoCustomUnion : dataTableInfoDTO.getList()) {
                for (DataSetTableUnionDTO dto : list) {
                    // 被关联表和自助数据集的表相等
                    if (StringUtils.equals(dto.getTargetTableId(), dataTableInfoCustomUnion.getTableId())) {
                        DatasetTableField sourceField = dataSetTableFieldsService.get(dto.getSourceTableFieldId());
                        DatasetTableField targetField = dataSetTableFieldsService.get(dto.getTargetTableFieldId());
                        if (ObjectUtils.isEmpty(sourceField) || ObjectUtils.isEmpty(targetField)) {
                            BaseException.throwException(ResultCode.get("dataset_field_delete"));
                        }
                        DatasetTable sourceTable = datasetTableMapper.selectById(dto.getSourceTableId());
                        String sourceTableName = JsonUtil.toJavaObj(sourceTable.getInfo(), DataTableInfoDTO.class)
                                .getTable();
                        DatasetTable targetTable = datasetTableMapper.selectById(dto.getTargetTableId());
                        String targetTableName = JsonUtil.toJavaObj(targetTable.getInfo(), DataTableInfoDTO.class)
                                .getTable();
                        join.append(convertUnionTypeToSQL(dto.getSourceUnionRelation()))
                                .append(String.format(keyword, targetTableName))
                                .append(" ON ")
                                .append(String.format(keyword, sourceTableName)).append(".")
                                .append(String.format(keyword, sourceField.getOriginName()))
                                .append(" = ")
                                .append(String.format(keyword, targetTableName)).append(".")
                                .append(String.format(keyword, targetField.getOriginName()));
                    }
                }
            }
            if (StringUtils.isEmpty(f)) {
                BaseException.throwException(ResultCode.get("custom_ds_delete"));
            }
            return MessageFormat.format("SELECT {0} FROM {1}", f, String.format(keyword, tableName)) + join.toString();
        } else {
            if (StringUtils.isEmpty(StringUtils.join(customInfo.get(tableName), ","))) {
                BaseException.throwException(ResultCode.get("custom_ds_delete"));
            }
            return MessageFormat.format("SELECT {0} FROM {1}", StringUtils.join(customInfo.get(tableName), ","),
                    String.format(keyword, tableName));
        }
    }
    /**
     * sql连接类型
     *
     * @param unionType
     * @return
     */
    private String convertUnionTypeToSQL(String unionType) {
        switch (unionType) {
            case "1:1":
            case "inner":
                return " INNER JOIN ";
            case "1:N":
            case "left":
                return " LEFT JOIN ";
            case "N:1":
            case "right":
                return " RIGHT JOIN ";
            case "N:N":
            case "full":
                return " FULL JOIN ";
            default:
                return " INNER JOIN ";
        }
    }


    /**
     * 递归获取表头字段id
     * @return
     */
    public static List<String> getTableHeadS(List<TableHeadRequest> tableHeadS){
        List<String> ret = new ArrayList<>();
        for (TableHeadRequest tableHead : tableHeadS) {
            ret.add(tableHead.getHead());
            if (CollectionUtils.isNotEmpty(tableHead.getChild())){
                ret.addAll(getTableHeadS(tableHead.getChild()));
            }
        }
        return ret;
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
