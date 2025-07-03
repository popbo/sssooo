package com.stdc.visual.dynamic.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionConstants;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.*;
import com.stdc.visual.dynamic.base.chart.dto.ChartCustomFilterItemDTO;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.dto.*;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetEnumRequest;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes;
import com.stdc.visual.dynamic.base.datasource.dto.TableFiled;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.base.datasource.request.DatasourceRequest;
import com.stdc.visual.dynamic.mapper.DatasetTableFieldMapper;
import com.stdc.visual.dynamic.mapper.DatasetTableMapper;
import com.stdc.visual.dynamic.mapper.DatasourceMapper;
import com.stdc.visual.dynamic.provider.ProviderFactory;
import com.stdc.visual.dynamic.provider.datasource.DatasourceProvider;
import com.stdc.visual.dynamic.provider.datasource.JdbcProvider;
import com.stdc.visual.dynamic.provider.query.DDLProvider;
import com.stdc.visual.dynamic.provider.query.QueryProvider;
import com.stdc.visual.dynamic.service.DataSetTableFieldsService;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.dynamic.service.DataSetTableUnionService;
import com.stdc.visual.dynamic.service.PermissionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/17--14:34
 * @describe: 数据表
 */
@Service
public class DataSetTableServiceImpl implements DataSetTableService {

    @Resource
    private DatasetTableMapper datasetTableMapper;

    @Resource
    private DatasourceMapper datasourceMapper;

    @Resource
    private DataSetTableFieldsService dataSetTableFieldsService;

    @Resource
    private CommonThreadPool commonThreadPool;

    @Resource
    private DataSetTableUnionService dataSetTableUnionService;

    @Resource
    private DatasetTableFieldMapper datasetTableFieldMapper;

    @Resource
    private PermissionService permissionService;

    @Autowired
    private RoleSourceService roleSourceService;


    @Override
    public void batchInsert(List<DataSetTableRequest> datasetTable) throws Exception {
        for (DataSetTableRequest table : datasetTable) {
            save(table);
        }
    }

    @Override
    public void saveExcel(DataSetTableRequest datasetTable) throws Exception {

    }

    @Override
    public DatasetTable save(DataSetTableRequest datasetTable) throws Exception {
        checkName(datasetTable);
        if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "sql")) {
            DataSetTableRequest dataSetTableRequest = new DataSetTableRequest();
            BeanUtils.copyBean(dataSetTableRequest, datasetTable);
            getSQLPreview(dataSetTableRequest);
        }
        if (StringUtils.isEmpty(datasetTable.getId())) {
            String id = StringUtil.randomUUID();
            datasetTable.setId(id);
            datasetTable.setCreateBy(AuthUtils.getUser().getUsername());
            datasetTable.setCreateTime(System.currentTimeMillis());
            int insert = datasetTableMapper.insert(datasetTable);
            // 添加表成功后，获取当前表字段和类型，抽象到dataease数据库
            if (insert == 1) {
                //保存角色-资源
                roleSourceService.saveRoleSource(id, SourceType.DATA_SET);
                saveTableField(datasetTable);
            }
        } else {
            int update = datasetTableMapper.updateById(datasetTable);
            if (datasetTable.getIsRename() == null || !datasetTable.getIsRename()) {
                // 更新数据和字段
                if (update == 1) {
                    if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "sql")
                            || StringUtils.equalsIgnoreCase(datasetTable.getType(), "custom")
                            || StringUtils.equalsIgnoreCase(datasetTable.getType(), "union")) {
                        saveTableField(datasetTable);
                    }
                }
            }
        }
        return datasetTable;
    }

    @Override
    public void alter(DataSetTableRequest request) throws Exception {
        checkName(request);
        datasetTableMapper.updateById(request);
    }

    @Override
    public void delete(String id){
        DatasetTable table = datasetTableMapper.selectById(id);
        datasetTableMapper.deleteById(id);
        dataSetTableFieldsService.deleteByTableId(id);
        // 删除关联关系
        dataSetTableUnionService.deleteUnionByTableId(id);
        try {
            // 抽取的数据集删除doris
            if (table.getMode() == 1) {
                deleteDorisTable(id, table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DataSetTableDTO> list(DataSetTableRequest dataSetTableRequest){
        dataSetTableRequest.setTypeFilter(dataSetTableRequest.getTypeFilter());
        List<DataSetTableDTO> search = datasetTableMapper.search(dataSetTableRequest);
        return search;
    }

    @Override
    public List<DatasetTable> list(List<String> datasetIds) {
        QueryWrapper<DatasetTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", datasetIds);
        return datasetTableMapper.selectList(queryWrapper);
    }

    @Override
    public List<DataSetTableDTO> listAndGroup(DataSetTableRequest dataSetTableRequest) {
        return null;
    }

    @Override
    public List<DataSetTableDTO> search(DataSetTableRequest dataSetTableRequest) {
        return null;
    }

    @Override
    public DatasetTable get(String id) {
        return datasetTableMapper.selectById(id);
    }

    @Override
    public List<TableFiled> getFields(DatasetTable datasetTable) throws Exception {
        Datasource ds = datasourceMapper.selectById(datasetTable.getDataSourceId());
        DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(ds);
        datasourceRequest.setTable(JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class).getTable());
        return datasourceProvider.getTableFields(datasourceRequest);
    }

    @Override
    public Map<String, List<DatasetTableField>> getFieldsFromDE(DataSetTableRequest dataSetTableRequest) throws Exception {
        DatasetTableField datasetTableField = DatasetTableField.builder().build();
        datasetTableField.setTableId(dataSetTableRequest.getId());
        datasetTableField.setChecked(Boolean.TRUE);
        List<DatasetTableField> fields = dataSetTableFieldsService.list(datasetTableField);
        List<DatasetTableField> dimension = new ArrayList<>();
        List<DatasetTableField> quota = new ArrayList<>();

        fields.forEach(field -> {
            if (StringUtils.equalsIgnoreCase("q", field.getGroupType())) {
                quota.add(field);
            } else {
                dimension.add(field);
            }
        });
        // quota add count
        DatasetTableField count = DatasetTableField.builder()
                .id("count")
                .tableId(dataSetTableRequest.getId())
                .originName("*")
                .name("记录数*")
                .dataeaseName("*")
                .type("INT")
                .checked(true)
                .columnIndex(999)
                .deType(2)
                .extField(1)
                .groupType("q")
                .build();
        quota.add(count);

        Map<String, List<DatasetTableField>> map = new HashMap<>();
        map.put("dimension", dimension);
        map.put("quota", quota);

        return map;
    }

    @Override
    public Map<String, Object> getPreviewData(DataSetTableRequest dataSetTableRequest, Integer page, Integer pageSize, List<DatasetTableField> extFields) throws Exception {
        Map<String, Object> map = new HashMap<>();
        DatasetTableField datasetTableField = DatasetTableField.builder().tableId(dataSetTableRequest.getId())
                .checked(Boolean.TRUE).build();
        List<DatasetTableField> fields = dataSetTableFieldsService.list(datasetTableField, true);
        //筛选请求参数
        if (ObjectUtils.isNotEmpty(dataSetTableRequest.getFieldIDS())){
            List<String> fieldIDS = Arrays.asList(dataSetTableRequest.getFieldIDS().split(","));
            fields = fields.stream().filter(f -> fieldIDS.contains(f.getId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(extFields)) {
            fields.addAll(extFields);
        }
        if (CollectionUtils.isEmpty(fields)) {
            map.put("fields", fields);
            map.put("data", new ArrayList<>());
            map.put("page", new DataSetPreviewPage());
            return map;
        }
        DatasetTable datasetTable = datasetTableMapper.selectById(dataSetTableRequest.getId());
        // 进行列权限筛选
        // 列权限
        // 脱敏权限
        List<String> desensitizationList = new ArrayList<>();
        fields = permissionService.filterColumnPermissions(fields,desensitizationList,datasetTable.getId());
        //经过行权限筛选后fields为空，当前用户无权限访问，返回空
        if (CollectionUtils.isEmpty(fields)) {
            BaseException.throwException(ResultCode.get("user_does_not_have_permission_to_access_field"));
        }
        if (CollectionUtils.isEmpty(fields)) {
            map.put("fields", fields);
            map.put("data", new ArrayList<>());
            map.put("page", new DataSetPreviewPage());
            return map;
        }
        // 行权限
        List<ChartFieldCustomFilterDTO> customFilter = new ArrayList<>();
        //TODO 进行行权限筛选
        customFilter = permissionService.filterRowPermissions(dataSetTableRequest,fields, datasetTable);
        //添加前端传来的where查询条件
        List<ChartFieldCustomFilterDTO> filterS = dataSetTableRequest.getWhereCustomFilterS();
        //初始化维度、度量、图例，目的是为了将维度、度量、图例条件传入sql
        // 没有条件的话，默认设置一个空的where子句条件,用来传递度量、维度、图例参数
        if (filterS == null || filterS.size() == 0) {
            filterS = filterS == null ? new ArrayList<>(1) : filterS;
            filterS.add(new ChartFieldCustomFilterDTO());
        }
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
        if (filterS.size() > 0) {
            filterS.forEach(fieldFilter -> {
                DataSetTableRequest superResult = new DataSetTableRequest();
                BeanUtils.copyBean(superResult, dataSetTableRequest);
                fieldFilter.setSuperRequest(superResult);
            });
        }
        if (filterS != null && filterS.size() > 0) {
            //遍历条件
            for (ChartFieldCustomFilterDTO filter : filterS) {
                //通过id主键获取到完整DatasetTableField,并重新赋值
                if (ObjectUtils.isNotEmpty(filter.getField())) {
                    filter.setField(dataSetTableFieldsService.get(filter.getField().getId()));
                }
            }
            customFilter.addAll(filterS);
        }
        String[] fieldArray = fields.stream().map(DatasetTableField::getDataeaseName).toArray(String[]::new);

        DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(dataSetTableRequest.getInfo(), DataTableInfoDTO.class);

        List<String[]> data = new ArrayList<>();
        DataSetPreviewPage dataSetPreviewPage = new DataSetPreviewPage();
        dataSetPreviewPage.setShow(Integer.valueOf(dataSetTableRequest.getRow()));
        dataSetPreviewPage.setPage(page);
        dataSetPreviewPage.setPageSize(pageSize);
        int realSize = Integer.parseInt(dataSetTableRequest.getRow()) < pageSize
                ? Integer.parseInt(dataSetTableRequest.getRow())
                : pageSize;
        if (page == Integer.parseInt(dataSetTableRequest.getRow()) / pageSize + 1) {
            realSize = Integer.parseInt(dataSetTableRequest.getRow()) % pageSize;
        }
        if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "db")) {
            if (datasetTable.getMode() == 0) {
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    throw new RuntimeException(ResultCode.get("datasource_delete"));
                }
                if (StringUtils.isNotEmpty(ds.getStatus()) && ds.getStatus().equalsIgnoreCase("Error")) {
                    throw new Exception(ResultCode.get("invalid_ds"));
                }
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                String table = dataTableInfoDTO.getTable();
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQueryTableWithPage(table, fields, page, pageSize, realSize, false, ds, customFilter));

                map.put("sql", datasourceRequest.getQuery());
                datasourceRequest.setPage(page);
                datasourceRequest.setFetchSize(Integer.parseInt(dataSetTableRequest.getRow()));
                datasourceRequest.setPageSize(pageSize);
                datasourceRequest.setRealSize(realSize);
                datasourceRequest.setPreviewData(true);
                try {
                    datasourceRequest.setPageable(true);
                    data.addAll(datasourceProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }

                try {
                    datasourceRequest.setQuery(qp.createQueryTableWithLimit(table, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, ds, customFilter));
                    datasourceRequest.setPageable(false);
                    dataSetPreviewPage.setTotal(datasourceProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            } else {
                // check doris table
                if (!checkDorisTableIsExists(dataSetTableRequest.getId())) {
                    throw new RuntimeException(ResultCode.get("data_not_sync"));
                }
                Datasource ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
                JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                String table = TableUtils.dorisName(dataSetTableRequest.getId());
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQueryTableWithPage(table, fields, page, pageSize, realSize, false, ds, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                try {
                    data.addAll(jdbcProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                try {
                    datasourceRequest.setQuery(qp.createQueryTableWithLimit(table, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, ds, customFilter));
                    dataSetPreviewPage.setTotal(jdbcProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }

        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "sql")) {
            if (datasetTable.getMode() == 0) {
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    throw new RuntimeException(ResultCode.get("datasource_delete"));
                }
                if (StringUtils.isNotEmpty(ds.getStatus()) && ds.getStatus().equalsIgnoreCase("Error")) {
                    throw new Exception(ResultCode.get("invalid_ds"));
                }
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);

                String sql = dataTableInfoDTO.getSql();
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQuerySQLWithPage(sql, fields, page, pageSize, realSize, false, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                datasourceRequest.setPage(page);
                datasourceRequest.setFetchSize(Integer.parseInt(dataSetTableRequest.getRow()));
                datasourceRequest.setPageSize(pageSize);
                datasourceRequest.setRealSize(realSize);
                datasourceRequest.setPreviewData(true);
                try {
                    datasourceRequest.setPageable(true);
                    data.addAll(datasourceProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                try {
                    datasourceRequest.setPageable(false);
                    datasourceRequest.setQuery(qp.createQuerySqlWithLimit(sql, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, customFilter));
                    dataSetPreviewPage.setTotal(datasourceProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            } else {
                // check doris table
                if (!checkDorisTableIsExists(dataSetTableRequest.getId())) {
                    throw new RuntimeException(ResultCode.get("data_not_sync"));
                }
                Datasource ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
                JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                String table = TableUtils.dorisName(dataSetTableRequest.getId());
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQueryTableWithPage(table, fields, page, pageSize, realSize, false, ds, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                try {
                    data.addAll(jdbcProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                try {
                    datasourceRequest.setQuery(qp.createQueryTableWithLimit(table, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, ds, customFilter));
                    dataSetPreviewPage.setTotal(jdbcProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "excel")) {
            if (!checkDorisTableIsExists(dataSetTableRequest.getId())) {
                throw new RuntimeException(ResultCode.get("data_not_sync"));
            }

            Datasource ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
            JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setDatasource(ds);
            String table = TableUtils.dorisName(dataSetTableRequest.getId());
            QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
            datasourceRequest.setQuery(
                    qp.createQueryTableWithPage(table, fields, page, pageSize, realSize, false, ds, customFilter));
            map.put("sql", datasourceRequest.getQuery());
            try {
                data.addAll(jdbcProvider.getData(datasourceRequest));
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
            }
            try {
                datasourceRequest.setQuery(qp.createQueryTableWithLimit(table, fields,
                        Integer.valueOf(dataSetTableRequest.getRow()), false, ds, customFilter));
                dataSetPreviewPage.setTotal(jdbcProvider.getData(datasourceRequest).size());
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
            }
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "custom")) {
            if (datasetTable.getMode() == 0) {
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    throw new RuntimeException(ResultCode.get("datasource_delete"));
                }
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);

                DataTableInfoDTO dt = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);
                List<DataSetTableUnionDTO> list = dataSetTableUnionService
                        .listByTableId(dt.getList().get(0).getTableId());

                String sql = "";
                try {
                    sql = getCustomSQLDatasource(dt, list, ds);
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQuerySQLWithPage(sql, fields, page, pageSize, realSize, false, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                datasourceRequest.setPage(page);
                datasourceRequest.setFetchSize(Integer.parseInt(dataSetTableRequest.getRow()));
                datasourceRequest.setPageSize(pageSize);
                datasourceRequest.setRealSize(realSize);
                datasourceRequest.setPreviewData(true);
                try {
                    datasourceRequest.setPageable(true);
                    data.addAll(datasourceProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                try {
                    datasourceRequest.setPageable(false);
                    datasourceRequest.setQuery(qp.createQuerySqlWithLimit(sql, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, customFilter));
                    dataSetPreviewPage.setTotal(datasourceProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            } else {
                Datasource ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
                JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                String table = TableUtils.dorisName(dataSetTableRequest.getId());
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQueryTableWithPage(table, fields, page, pageSize, realSize, false, ds, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                try {
                    data.addAll(jdbcProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }

                try {
                    datasourceRequest.setQuery(qp.createQueryTableWithLimit(table, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, ds, customFilter));
                    dataSetPreviewPage.setTotal(jdbcProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "union")) {
            if (datasetTable.getMode() == 0) {
                Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
                if (ObjectUtils.isEmpty(ds)) {
                    BaseException.throwException(ResultCode.get("datasource_delete"));
                }
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);

                DataTableInfoDTO dt = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);

                String sql = "";
                try {
                    sql = (String) getUnionSQLDatasource(dt, ds).get("sql");
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQuerySQLWithPage(sql, fields, page, pageSize, realSize, false, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                datasourceRequest.setPage(page);
                datasourceRequest.setFetchSize(Integer.parseInt(dataSetTableRequest.getRow()));
                datasourceRequest.setPageSize(pageSize);
                datasourceRequest.setRealSize(realSize);
                datasourceRequest.setPreviewData(true);
                try {
                    datasourceRequest.setPageable(true);
                    data.addAll(datasourceProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
                try {
                    datasourceRequest.setPageable(false);
                    datasourceRequest.setQuery(qp.createQuerySqlWithLimit(sql, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, customFilter));
                    dataSetPreviewPage.setTotal(datasourceProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            } else {
                Datasource ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
                JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                String table = TableUtils.dorisName(dataSetTableRequest.getId());
                QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
                datasourceRequest.setQuery(
                        qp.createQueryTableWithPage(table, fields, page, pageSize, realSize, false, ds, customFilter));
                map.put("sql", datasourceRequest.getQuery());
                try {
                    data.addAll(jdbcProvider.getData(datasourceRequest));
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }

                try {
                    datasourceRequest.setQuery(qp.createQueryTableWithLimit(table, fields,
                            Integer.valueOf(dataSetTableRequest.getRow()), false, ds, customFilter));
                    dataSetPreviewPage.setTotal(jdbcProvider.getData(datasourceRequest).size());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                    BaseException.throwException(ResultCode.get("ds_error") + "->" + e.getMessage());
                }
            }
        }

        List<Map<String, Object>> jsonArray = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(data)) {
            jsonArray = data.stream().map(ele -> {
                Map<String, Object> tmpMap = new HashMap<>();
                for (int i = 0; i < ele.length; i++) {
                    if (desensitizationList.contains(fieldArray[i])) {
                        tmpMap.put(fieldArray[i], ColumnPermissionConstants.DesensitizationTemplate);
                    } else {
                        tmpMap.put(fieldArray[i], ele[i]);
                    }
                }
                return tmpMap;
            }).collect(Collectors.toList());
        }

        if (!map.containsKey("status")) {
            map.put("status", "success");
        }
        map.put("fields", fields);
        map.put("data", jsonArray);
        map.put("page", dataSetPreviewPage);
        return map;
    }
    @Override
    public Map<String, Object> getSQLPreview(DataSetTableRequest dataSetTableRequest) throws Exception {
        Datasource ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
        DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(ds);
        String sql = JSONObject.parseObject(dataSetTableRequest.getInfo()).getString("sql");

        if (StringUtils.isEmpty(sql)) {
            BaseException.throwException(ResultCode.get("sql_not_empty"));
        }
        QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
        String sqlAsTable = qp.createSQLPreview(sql, null);
        datasourceRequest.setQuery(sqlAsTable);
        Map<String, List> result = datasourceProvider.fetchResultAndField(datasourceRequest);
        List<String[]> data = result.get("dataList");
        List<TableFiled> fields = result.get("fieldList");
        String[] fieldArray = fields.stream().map(TableFiled::getFieldName).toArray(String[]::new);
        if (checkIsRepeat(fieldArray)) {
            BaseException.throwException(ResultCode.get("excel_field_repeat"));
        }
        List<Map<String, Object>> jsonArray = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(data)) {
            jsonArray = data.stream().map(ele -> {
                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < ele.length; i++) {
                    map.put(fieldArray[i], ele[i]);
                }
                return map;
            }).collect(Collectors.toList());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("fields", fields);
        map.put("data", jsonArray);

        return map;
    }

    @Override
    public List<String> getEnumPreview(DataSetEnumRequest request) throws Exception {
        //获取字段枚举值sql模板
        final String getEnumPreviewSqlTemplate = "SELECT DISTINCT(%s) FROM `%s` ORDER BY `%s`";
        DataSetTableRequest sqlRequest = new DataSetTableRequest();
        sqlRequest.setDataSourceId(request.getDataSourceId());
        sqlRequest.setType("sql");
        DatasetTable datasetTable = datasetTableMapper.selectOne(new LambdaQueryWrapper<DatasetTable>().eq(DatasetTable::getId, request.getDataSetTableId()));
        DatasetTableField field = datasetTableFieldMapper.selectOne(new LambdaQueryWrapper<DatasetTableField>().eq(DatasetTableField::getId, request.getDataSetTableFieldId()));
        String table = null;
        DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);
        if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "db")) {
            table = dataTableInfoDTO.getTable();
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "sql")) {
            table = dataTableInfoDTO.getSql();
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "custom")) {
            List<DataSetTableUnionDTO> list = dataSetTableUnionService
                    .listByTableId(dataTableInfoDTO.getList().get(0).getTableId());
            Datasource ds = datasourceMapper.selectById(request.getDataSourceId());
            table = getCustomSQLDatasource(dataTableInfoDTO, list, ds);
        }
        if (ObjectUtil.isEmpty(table)) BaseException.throwException(ResultCode.get("ds_error"));
        Datasource ds = datasourceMapper.selectById(sqlRequest.getDataSourceId());
        DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(ds);
        String sql = String.format(getEnumPreviewSqlTemplate,field.getOriginName(),table,field.getOriginName());
        if (StringUtils.isEmpty(sql)) BaseException.throwException(ResultCode.get("sql_not_empty"));
        datasourceRequest.setQuery(sql);
        Map<String, List> result = datasourceProvider.fetchResultAndField(datasourceRequest);
        List<String> data = new ArrayList<>();
        result.get("dataList").forEach(d->{ data.add(((String[])d)[0]); });
        return data;
    }

    @Override
    public Map<String, Object> getUnionPreview(DataSetTableRequest dataSetTableRequest) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getCustomPreview(DataSetTableRequest dataSetTableRequest) throws Exception {
        DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(dataSetTableRequest.getInfo(), DataTableInfoDTO.class);
        List<DataSetTableUnionDTO> list = dataSetTableUnionService
                .listByTableId(dataTableInfoDTO.getList().get(0).getTableId());
        String sql;

        DatasourceRequest datasourceRequest = new DatasourceRequest();
        Datasource ds;
        if (dataSetTableRequest.getMode() == 0) {
            ds = datasourceMapper.selectById(dataSetTableRequest.getDataSourceId());
            datasourceRequest.setDatasource(ds);
            sql = getCustomSQLDatasource(dataTableInfoDTO, list, ds);
        } else {
            ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
            datasourceRequest.setDatasource(ds);
            sql = getCustomSQLDoris(dataTableInfoDTO, list);
        }
        Map<String, Object> res = new HashMap<>();
        try {
            DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
            QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
            datasourceRequest.setQuery(qp.createSQLPreview(sql, null));
            Map<String, List> result = datasourceProvider.fetchResultAndField(datasourceRequest);
            List<String[]> data = result.get("dataList");
            List<TableFiled> fields = result.get("fieldList");
            String[] fieldArray = fields.stream().map(TableFiled::getFieldName).toArray(String[]::new);

            List<Map<String, Object>> jsonArray = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(data)) {
                jsonArray = data.stream().map(ele -> {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < ele.length; i++) {
                        map.put(fieldArray[i], ele[i]);
                    }
                    return map;
                }).collect(Collectors.toList());
            }

            // 获取每个字段在当前de数据库中的name，作为sql查询后的remarks返回前端展示
            List<DatasetTableField> checkedFieldList = new ArrayList<>();
            dataTableInfoDTO.getList().forEach(
                    ele -> checkedFieldList.addAll(dataSetTableFieldsService.getListByIds(ele.getCheckedFields())));
            for (DatasetTableField datasetTableField : checkedFieldList) {
                for (TableFiled tableFiled : fields) {
                    if (StringUtils.equalsIgnoreCase(tableFiled.getFieldName(),
                            TableUtils.dorisFieldName(
                                    datasetTableField.getTableId() + "_" + datasetTableField.getDataeaseName()))
                            || StringUtils.equalsIgnoreCase(tableFiled.getFieldName(), TableUtils.dorisFieldName(
                            datasetTableField.getTableId() + "_" + datasetTableField.getOriginName()))) {
                        tableFiled.setRemarks(datasetTableField.getName());
                        break;
                    }
                }
            }

            res.put("fields", fields);
            res.put("data", jsonArray);
            return res;
        } catch (Exception e) {
            return res;
        }
    }

    @Override
    public DatasetTable syncDatasetTableField(String id) throws Exception {
        DatasetTable datasetTable = datasetTableMapper.selectById(id);
        saveTableField(datasetTable);
        return datasetTable;
    }

    @Override
    public DataSetDetail getDatasetDetail(String id) {
        DataSetDetail dataSetDetail = new DataSetDetail();
        DatasetTable table = datasetTableMapper.selectById(id);
        dataSetDetail.setTable(table);
        if (ObjectUtils.isNotEmpty(table)) {
            Datasource datasource = datasourceMapper.selectById(table.getDataSourceId());
            Optional.ofNullable(datasource).orElse(new Datasource()).setConfiguration(null);
            dataSetDetail.setDatasource(datasource);
        }
        return dataSetDetail;
    }

    /**
     * 保存数据表字段
     *
     * @param datasetTable
     * @throws Exception
     */
    public void saveTableField(DatasetTable datasetTable) throws Exception {
        Datasource ds = datasourceMapper.selectById(datasetTable.getDataSourceId());
        DataSetTableRequest dataSetTableRequest = new DataSetTableRequest();
        BeanUtils.copyBean(dataSetTableRequest, datasetTable);

        List<TableFiled> fields = new ArrayList<>();
        long syncTime = System.currentTimeMillis();
        if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "db")) {
            fields = getFields(datasetTable);
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "sql")) {
            DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setDatasource(ds);
            QueryProvider qp = ProviderFactory.getQueryProvider(ds.getType());
            String sqlAsTable = qp.createSQLPreview(
                    JsonUtil.toJavaObj(dataSetTableRequest.getInfo(), DataTableInfoDTO.class).getSql(), null);
            datasourceRequest.setQuery(sqlAsTable);
            fields = datasourceProvider.fetchResultField(datasourceRequest);
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "excel")) {
            fields = dataSetTableRequest.getFields();
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "custom")) {
            if (datasetTable.getMode() == 1) {
                // save field
                DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(dataSetTableRequest.getInfo(),
                        DataTableInfoDTO.class);
                List<DataTableInfoCustomUnion> list = dataTableInfoDTO.getList();
                List<DatasetTableField> fieldList = new ArrayList<>();
                list.forEach(ele -> {
                    List<DatasetTableField> listByIds = dataSetTableFieldsService
                            .getListByIdsEach(ele.getCheckedFields());
                    listByIds.forEach(f -> f.setDataeaseName(
                            TableUtils.dorisFieldName(ele.getTableId() + "_" + f.getDataeaseName())));
                    fieldList.addAll(listByIds);
                });
                for (int i = 0; i < fieldList.size(); i++) {
                    DatasetTableField datasetTableField = fieldList.get(i);
                    datasetTableField.setId(null);
                    datasetTableField.setTableId(datasetTable.getId());
                    datasetTableField.setColumnIndex(i);
                }
                dataSetTableFieldsService.deleteByTableId(datasetTable.getId());
                dataSetTableFieldsService.batchEdit(fieldList);
                // custom 创建doris视图
                if (datasetTable.getMode() == 1) {
//                    createDorisView(TableUtils.dorisName(datasetTable.getId()), getCustomSQLDoris(dataTableInfoDTO,
//                            dataSetTableUnionService.listByTableId(dataTableInfoDTO.getList().get(0).getTableId())));
                }
                return;
            } else {
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                DataTableInfoDTO dt = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);
                List<DataSetTableUnionDTO> list = dataSetTableUnionService
                        .listByTableId(dt.getList().get(0).getTableId());
                String sqlAsTable = getCustomSQLDatasource(dt, list, ds);
                datasourceRequest.setQuery(sqlAsTable);
                fields = datasourceProvider.fetchResultField(datasourceRequest);

                DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(dataSetTableRequest.getInfo(),
                        DataTableInfoDTO.class);
                List<DataTableInfoCustomUnion> listField = dataTableInfoDTO.getList();
                List<DatasetTableField> fieldList = new ArrayList<>();
                listField.forEach(ele -> {
                    List<DatasetTableField> listByIds = dataSetTableFieldsService
                            .getListByIdsEach(ele.getCheckedFields());
                    fieldList.addAll(listByIds);
                });
                for (DatasetTableField field : fieldList) {
                    for (TableFiled tableFiled : fields) {
                        if (StringUtils.equalsIgnoreCase(
                                TableUtils.dorisFieldName(field.getTableId() + "_" + field.getOriginName()),
                                tableFiled.getFieldName())) {
                            tableFiled.setRemarks(field.getName());
                            break;
                        }
                    }
                }
            }
        } else if (StringUtils.equalsIgnoreCase(datasetTable.getType(), "union")) {
            if (datasetTable.getMode() == 1) {
                ds = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                // save field
                DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(dataSetTableRequest.getInfo(),
                        DataTableInfoDTO.class);
                Map<String, Object> sqlMap = getUnionSQLDoris(dataTableInfoDTO);
                String sql = (String) sqlMap.get("sql");
                List<DatasetTableField> fieldList = (List<DatasetTableField>) sqlMap.get("field");
                List<UnionParamDTO> join = (List<UnionParamDTO>) sqlMap.get("join");

                // custom 创建doris视图
                createDorisView(TableUtils.dorisName(datasetTable.getId()), sql);

                datasourceRequest.setQuery(sql);
                fields = datasourceProvider.fetchResultField(datasourceRequest);
                for (DatasetTableField field : fieldList) {
                    for (TableFiled tableFiled : fields) {
                        if (StringUtils.equalsIgnoreCase(
                                TableUtils.dorisFieldName(field.getTableId() + "_" + field.getDataeaseName()),
                                tableFiled.getFieldName())) {
                            tableFiled.setRemarks(field.getName());
                            break;
                        }
                    }
                }
            } else {
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(ds);
                DataTableInfoDTO dt = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);

                Map<String, Object> sqlMap = getUnionSQLDatasource(dt, ds);
                String sql = (String) sqlMap.get("sql");
                List<DatasetTableField> fieldList = (List<DatasetTableField>) sqlMap.get("field");
                List<UnionParamDTO> join = (List<UnionParamDTO>) sqlMap.get("join");

                datasourceRequest.setQuery(sql);
                fields = datasourceProvider.fetchResultField(datasourceRequest);

                for (DatasetTableField field : fieldList) {
                    for (TableFiled tableFiled : fields) {
                        if (StringUtils.equalsIgnoreCase(
                                TableUtils.dorisFieldNameShort(field.getTableId() + "_" + field.getOriginName()),
                                tableFiled.getFieldName())) {
                            tableFiled.setRemarks(field.getName());
                            break;
                        }
                    }
                }
            }
        }
        QueryProvider qp = null;
        if (!ObjectUtils.isEmpty(ds)) {
            qp = ProviderFactory.getQueryProvider(ds.getType());
        }
        if (CollectionUtils.isNotEmpty(fields)) {
            List<String> originNameList = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                TableFiled filed = fields.get(i);
                originNameList.add(filed.getFieldName());
                DatasetTableField datasetTableField = DatasetTableField.builder().build();
                // 物理字段名设定为唯一，查询当前数据集下是否已存在该字段，存在则update，不存在则insert
                // 字段名一致，认为字段没有改变
                QueryWrapper<DatasetTableField> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("table_id", datasetTable.getId()).eq("origin_name", filed.getFieldName());
                List<DatasetTableField> datasetTableFields = datasetTableFieldMapper.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(datasetTableFields)) {
                    datasetTableField.setId(datasetTableFields.get(0).getId());
                    datasetTableField.setOriginName(filed.getFieldName());
                    datasetTableField.setType(filed.getFieldType());
                    datasetTableField.setSize(filed.getFieldSize());
                    if (ObjectUtils.isEmpty(ds)) {
                        datasetTableField.setDeExtractType(transFieldType(filed.getFieldType()));
                    } else {
                        Integer fieldType = qp.transFieldType(filed.getFieldType());
                        datasetTableField.setDeExtractType(fieldType);
                    }
                } else {
                    datasetTableField.setTableId(datasetTable.getId());
                    datasetTableField.setOriginName(filed.getFieldName());
                    datasetTableField.setName(filed.getRemarks());
                    if (datasetTable.getMode() == 1 && StringUtils.equalsIgnoreCase("union", datasetTable.getType())) {
                        datasetTableField.setDataeaseName(filed.getFieldName());
                    } else {
                        datasetTableField.setDataeaseName(TableUtils.columnName(filed.getFieldName()));
                    }
                    datasetTableField.setType(filed.getFieldType());
                    if (ObjectUtils.isEmpty(ds)) {
                        datasetTableField.setDeType(transFieldType(filed.getFieldType()));
                        datasetTableField.setDeExtractType(transFieldType(filed.getFieldType()));
                    } else {
                        Integer fieldType = qp.transFieldType(filed.getFieldType());
                        datasetTableField.setDeType(fieldType == 4 ? 2 : fieldType);
                        datasetTableField.setDeExtractType(fieldType);
                    }
                    datasetTableField.setSize(filed.getFieldSize());
                    datasetTableField.setChecked(true);
                    datasetTableField.setColumnIndex(i);
                    datasetTableField.setLastSyncTime(syncTime);
                    datasetTableField.setExtField(0);
                    datasetTableField.setGroupType(datasetTableField.getDeType() < 2 ? "d" : "q");
                }
                dataSetTableFieldsService.save(datasetTableField);
            }
            // delete 数据库中多余的字段
            datasetTableFieldMapper.delete(new QueryWrapper<DatasetTableField>()
                    .eq("table_id", datasetTable.getId())
                    .eq("ext_field", 0)
                    .notIn("origin_name", originNameList));
        }
    }


    public String getCustomSQLDatasource(DataTableInfoDTO dataTableInfoDTO, List<DataSetTableUnionDTO> list,
                                         Datasource ds) {
        DatasourceTypes datasourceTypes = DatasourceTypes.valueOf(ds.getType());
        String keyword = datasourceTypes.getKeywordPrefix() + "%s" + datasourceTypes.getKeywordSuffix();
        Map<String, String[]> customInfo = new TreeMap<>();
        for (DataTableInfoCustomUnion ele : dataTableInfoDTO.getList()) {
            DatasetTable datasetTable = datasetTableMapper.selectById(ele.getTableId());
            String table = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class).getTable();
            if (ObjectUtils.isEmpty(datasetTable)) {
                throw new RuntimeException(ResultCode.get("custom_ds_delete"));
            }
            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(ele.getCheckedFields());
            if (CollectionUtils.isEmpty(fields)) {
                throw new RuntimeException(ResultCode.get("cst_ds_tb_or_field_deleted"));
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
                throw new RuntimeException(ResultCode.get("custom_ds_delete"));
            }
            return MessageFormat.format("SELECT {0} FROM {1}", f, String.format(keyword, tableName)) + join.toString();
        } else {
            if (StringUtils.isEmpty(StringUtils.join(customInfo.get(tableName), ","))) {
                throw new RuntimeException(ResultCode.get("custom_ds_delete"));
            }
            return MessageFormat.format("SELECT {0} FROM {1}", StringUtils.join(customInfo.get(tableName), ","),
                    String.format(keyword, tableName));
        }
    }

    // 关联数据集从doris里预览数据
    private Map<String, Object> getUnionSQLDoris(DataTableInfoDTO dataTableInfoDTO) {
        List<UnionDTO> union = dataTableInfoDTO.getUnion();
        // 所有选中的字段，即select后的查询字段
        Map<String, String[]> checkedInfo = new LinkedHashMap<>();
        List<UnionParamDTO> unionList = new ArrayList<>();
        List<DatasetTableField> checkedFields = new ArrayList<>();
        String sql = "";
        for (UnionDTO unionDTO : union) {
            // doris 使用数据集id做表名，拼sql将用到该名称
            String tableId = unionDTO.getCurrentDs().getId();
            String table = TableUtils.dorisName(tableId);
            DatasetTable datasetTable = datasetTableMapper.selectById(tableId);
            if (ObjectUtils.isEmpty(datasetTable)) {
                BaseException.throwException(
                        ResultCode.get("custom_ds_delete") + String.format(":table id [%s]", tableId));
            }
            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(unionDTO.getCurrentDsField());

            String[] array = fields.stream()
                    .map(f -> table + "." + f.getDataeaseName() + " AS "
                            + TableUtils.dorisFieldName(tableId + "_" + f.getDataeaseName()))
                    .toArray(String[]::new);
            checkedInfo.put(table, array);
            checkedFields.addAll(fields);
            // 获取child的fields和union
            if (CollectionUtils.isNotEmpty(unionDTO.getChildrenDs())) {
                getUnionSQLDorisJoin(unionDTO.getChildrenDs(), checkedInfo, unionList, checkedFields);
            }
        }
        // build sql
        if (CollectionUtils.isNotEmpty(unionList)) {
            // field
            StringBuilder field = new StringBuilder();
            Iterator<Map.Entry<String, String[]>> iterator = checkedInfo.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String[]> next = iterator.next();
                field.append(StringUtils.join(next.getValue(), ",")).append(",");
            }
            String f = subPrefixSuffixChar(field.toString());
            // join
            StringBuilder join = new StringBuilder();
            for (UnionParamDTO unionParamDTO : unionList) {
                String joinType = convertUnionTypeToSQL(unionParamDTO.getUnionType());
                UnionItemDTO u = unionParamDTO.getUnionFields().get(0);
                DatasetTableField pField = dataSetTableFieldsService.get(u.getParentField().getId());
                DatasetTableField cField = dataSetTableFieldsService.get(u.getCurrentField().getId());
                if (ObjectUtils.isEmpty(pField) || ObjectUtils.isEmpty(cField)) {
                    BaseException.throwException(ResultCode.get("dataset_field_delete"));
                }
                DatasetTable parentTable = datasetTableMapper.selectById(pField.getTableId());
                DatasetTable currentTable = datasetTableMapper.selectById(cField.getTableId());

                join.append(" ").append(joinType).append(" ").append(TableUtils.dorisName(currentTable.getId()))
                        .append(" ON ");
                for (int i = 0; i < unionParamDTO.getUnionFields().size(); i++) {
                    UnionItemDTO unionItemDTO = unionParamDTO.getUnionFields().get(i);
                    // 通过field id取得field详情，并且以第一组为准，寻找dataset table
                    DatasetTableField parentField = dataSetTableFieldsService
                            .get(unionItemDTO.getParentField().getId());
                    DatasetTableField currentField = dataSetTableFieldsService
                            .get(unionItemDTO.getCurrentField().getId());

                    join.append(TableUtils.dorisName(parentTable.getId())).append(".")
                            .append(parentField.getDataeaseName())
                            .append(" = ")
                            .append(TableUtils.dorisName(currentTable.getId())).append(".")
                            .append(currentField.getDataeaseName());
                    if (i < unionParamDTO.getUnionFields().size() - 1) {
                        join.append(" AND ");
                    }
                }
            }
            if (StringUtils.isEmpty(f)) {
                BaseException.throwException(ResultCode.get("union_ds_no_checked"));
            }
            sql = MessageFormat.format("SELECT {0} FROM {1}", f,
                    TableUtils.dorisName(union.get(0).getCurrentDs().getId())) + join.toString();
        } else {
            String f = StringUtils.join(checkedInfo.get(TableUtils.dorisName(union.get(0).getCurrentDs().getId())),
                    ",");
            if (StringUtils.isEmpty(f)) {
                throw new RuntimeException(ResultCode.get("union_ds_no_checked"));
            }
            sql = MessageFormat.format("SELECT {0} FROM {1}", f,
                    TableUtils.dorisName(union.get(0).getCurrentDs().getId()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("field", checkedFields);
        map.put("join", unionList);
        return map;
    }

    // 递归计算出所有子级的checkedFields和unionParam
    private void getUnionSQLDorisJoin(List<UnionDTO> childrenDs, Map<String, String[]> checkedInfo,
                                      List<UnionParamDTO> unionList, List<DatasetTableField> checkedFields) {
        for (int i = 0; i < childrenDs.size(); i++) {
            UnionDTO unionDTO = childrenDs.get(i);
            String tableId = unionDTO.getCurrentDs().getId();
            String table = TableUtils.dorisName(tableId);
            DatasetTable datasetTable = datasetTableMapper.selectById(tableId);
            if (ObjectUtils.isEmpty(datasetTable)) {
                BaseException.throwException(
                        ResultCode.get("custom_ds_delete") + String.format(":table id [%s]", tableId));
            }
            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(unionDTO.getCurrentDsField());

            String[] array = fields.stream()
                    .map(f -> table + "." + f.getDataeaseName() + " AS "
                            + TableUtils.dorisFieldName(tableId + "_" + f.getDataeaseName()))
                    .toArray(String[]::new);
            checkedInfo.put(table, array);
            checkedFields.addAll(fields);

            unionList.add(unionDTO.getUnionToParent());
            if (CollectionUtils.isNotEmpty(unionDTO.getChildrenDs())) {
                getUnionSQLDorisJoin(unionDTO.getChildrenDs(), checkedInfo, unionList, checkedFields);
            }
        }
    }

    // 递归计算出所有子级的checkedFields和unionParam
    private void getUnionSQLDatasourceJoin(List<UnionDTO> childrenDs, Map<String, String[]> checkedInfo,
                                           List<UnionParamDTO> unionList, String keyword, List<DatasetTableField> checkedFields) {
        for (int i = 0; i < childrenDs.size(); i++) {
            UnionDTO unionDTO = childrenDs.get(i);

            DatasetTable datasetTable = datasetTableMapper.selectById(unionDTO.getCurrentDs().getId());
            String tableId = unionDTO.getCurrentDs().getId();
            if (ObjectUtils.isEmpty(datasetTable)) {
                BaseException.throwException(
                        ResultCode.get("custom_ds_delete") + String.format(":table id [%s]", tableId));
            }
            String table = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class).getTable();

            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(unionDTO.getCurrentDsField());

            String[] array = fields.stream()
                    .map(f -> String.format(keyword, table) + "." + String.format(keyword, f.getOriginName()) + " AS "
                            + TableUtils.dorisFieldNameShort(tableId + "_" + f.getOriginName()))
                    .toArray(String[]::new);
            checkedInfo.put(table, array);
            checkedFields.addAll(fields);

            unionList.add(unionDTO.getUnionToParent());
            if (CollectionUtils.isNotEmpty(unionDTO.getChildrenDs())) {
                getUnionSQLDatasourceJoin(unionDTO.getChildrenDs(), checkedInfo, unionList, keyword, checkedFields);
            }
        }
    }

    // 自助数据集从doris里预览数据
    private String getCustomSQLDoris(DataTableInfoDTO dataTableInfoDTO, List<DataSetTableUnionDTO> list) {
        Map<String, String[]> customInfo = new TreeMap<>();
        dataTableInfoDTO.getList().forEach(ele -> {
            String table = TableUtils.dorisName(ele.getTableId());
            DatasetTable datasetTable = datasetTableMapper.selectById(ele.getTableId());
            if (ObjectUtils.isEmpty(datasetTable)) {
                throw new RuntimeException(ResultCode.get("custom_ds_delete"));
            }
            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(ele.getCheckedFields());
            if (CollectionUtils.isEmpty(fields)) {
                throw new RuntimeException(ResultCode.get("cst_ds_tb_or_field_deleted"));
            }
            String[] array = fields.stream()
                    .map(f -> table + "." + f.getDataeaseName() + " AS "
                            + TableUtils.dorisFieldName(ele.getTableId() + "_" + f.getDataeaseName()))
                    .toArray(String[]::new);
            customInfo.put(table, array);
        });
        DataTableInfoCustomUnion first = dataTableInfoDTO.getList().get(0);
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

                        join.append(convertUnionTypeToSQL(dto.getSourceUnionRelation()))
                                .append(TableUtils.dorisName(dto.getTargetTableId()))
                                .append(" ON ")
                                .append(TableUtils.dorisName(dto.getSourceTableId())).append(".")
                                .append(sourceField.getDataeaseName())
                                .append(" = ")
                                .append(TableUtils.dorisName(dto.getTargetTableId())).append(".")
                                .append(targetField.getDataeaseName());
                    }
                }
            }
            if (StringUtils.isEmpty(f)) {
                throw new RuntimeException(ResultCode.get("custom_ds_delete"));
            }
            return MessageFormat.format("SELECT {0} FROM {1}", f, TableUtils.dorisName(first.getTableId()))
                    + join.toString();
        } else {
            if (StringUtils
                    .isEmpty(StringUtils.join(customInfo.get(TableUtils.dorisName(first.getTableId())), ","))) {
                throw new RuntimeException(ResultCode.get("custom_ds_delete"));
            }
            return MessageFormat.format("SELECT {0} FROM {1}",
                    StringUtils.join(customInfo.get(TableUtils.dorisName(first.getTableId())), ","),
                    TableUtils.dorisName(first.getTableId()));
        }
    }


    // 关联数据集 直连模式
    public Map<String, Object> getUnionSQLDatasource(DataTableInfoDTO dataTableInfoDTO, Datasource ds) {
        DatasourceTypes datasourceTypes = DatasourceTypes.valueOf(ds.getType());
        String keyword = datasourceTypes.getKeywordPrefix() + "%s" + datasourceTypes.getKeywordSuffix();

        List<UnionDTO> union = dataTableInfoDTO.getUnion();
        // 所有选中的字段，即select后的查询字段
        Map<String, String[]> checkedInfo = new LinkedHashMap<>();
        List<UnionParamDTO> unionList = new ArrayList<>();
        List<DatasetTableField> checkedFields = new ArrayList<>();
        String sql = "";
        String tableName = JsonUtil.toJavaObj(datasetTableMapper.selectById(union.get(0).getCurrentDs().getId()).getInfo(),
                DataTableInfoDTO.class)
                .getTable();
        for (UnionDTO unionDTO : union) {
            DatasetTable datasetTable = datasetTableMapper.selectById(unionDTO.getCurrentDs().getId());
            String table = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class).getTable();
            String tableId = unionDTO.getCurrentDs().getId();
            if (ObjectUtils.isEmpty(datasetTable)) {
                BaseException.throwException(
                        ResultCode.get("custom_ds_delete") + String.format(":table id [%s]", tableId));
            }
            List<DatasetTableField> fields = dataSetTableFieldsService.getListByIdsEach(unionDTO.getCurrentDsField());

            String[] array = fields.stream()
                    .map(f -> String.format(keyword, table) + "." + String.format(keyword, f.getOriginName()) + " AS "
                            + TableUtils.dorisFieldNameShort(tableId + "_" + f.getOriginName()))
                    .toArray(String[]::new);
            checkedInfo.put(table, array);
            checkedFields.addAll(fields);
            // 获取child的fields和union
            if (CollectionUtils.isNotEmpty(unionDTO.getChildrenDs())) {
                getUnionSQLDatasourceJoin(unionDTO.getChildrenDs(), checkedInfo, unionList, keyword, checkedFields);
            }
        }
        // build sql
        if (CollectionUtils.isNotEmpty(unionList)) {
            // field
            StringBuilder field = new StringBuilder();
            Iterator<Map.Entry<String, String[]>> iterator = checkedInfo.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String[]> next = iterator.next();
                field.append(StringUtils.join(next.getValue(), ",")).append(",");
            }
            String f = subPrefixSuffixChar(field.toString());
            // join
            StringBuilder join = new StringBuilder();
            for (UnionParamDTO unionParamDTO : unionList) {
                String joinType = convertUnionTypeToSQL(unionParamDTO.getUnionType());
                UnionItemDTO u = unionParamDTO.getUnionFields().get(0);
                DatasetTableField pField = dataSetTableFieldsService.get(u.getParentField().getId());
                DatasetTableField cField = dataSetTableFieldsService.get(u.getCurrentField().getId());
                if (ObjectUtils.isEmpty(pField) || ObjectUtils.isEmpty(cField)) {
                    BaseException.throwException(ResultCode.get("dataset_field_delete"));
                }
                DatasetTable parentTable = datasetTableMapper.selectById(pField.getTableId());
                String parentTableName = JsonUtil.toJavaObj(parentTable.getInfo(), DataTableInfoDTO.class).getTable();
                DatasetTable currentTable = datasetTableMapper.selectById(cField.getTableId());
                String currentTableName = JsonUtil.toJavaObj(currentTable.getInfo(), DataTableInfoDTO.class)
                        .getTable();

                join.append(" ").append(joinType).append(" ").append(String.format(keyword, currentTableName))
                        .append(" ON ");
                for (int i = 0; i < unionParamDTO.getUnionFields().size(); i++) {
                    UnionItemDTO unionItemDTO = unionParamDTO.getUnionFields().get(i);
                    // 通过field id取得field详情，并且以第一组为准，寻找dataset table
                    DatasetTableField parentField = dataSetTableFieldsService
                            .get(unionItemDTO.getParentField().getId());
                    DatasetTableField currentField = dataSetTableFieldsService
                            .get(unionItemDTO.getCurrentField().getId());

                    join.append(String.format(keyword, parentTableName)).append(".")
                            .append(String.format(keyword, parentField.getOriginName()))
                            .append(" = ")
                            .append(String.format(keyword, currentTableName)).append(".")
                            .append(String.format(keyword, currentField.getOriginName()));
                    if (i < unionParamDTO.getUnionFields().size() - 1) {
                        join.append(" AND ");
                    }
                }
            }
            if (StringUtils.isEmpty(f)) {
                BaseException.throwException(ResultCode.get("union_ds_no_checked"));
            }
            sql = MessageFormat.format("SELECT {0} FROM {1}", f, String.format(keyword, tableName)) + join.toString();
        } else {
            String f = StringUtils.join(checkedInfo.get(tableName), ",");
            if (StringUtils.isEmpty(f)) {
                throw new RuntimeException(ResultCode.get("union_ds_no_checked"));
            }
            sql = MessageFormat.format("SELECT {0} FROM {1}", f, String.format(keyword, tableName));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("sql", sql);
        map.put("field", checkedFields);
        map.put("join", unionList);
        return map;
    }

    private void createDorisView(String dorisTableName, String customSql) throws Exception {
        Datasource dorisDatasource = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
        JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(dorisDatasource);
        DDLProvider ddlProvider = ProviderFactory.getDDLProvider(dorisDatasource.getType());
        // 先删除表
        datasourceRequest.setQuery(ddlProvider.dropView(dorisTableName));
        jdbcProvider.exec(datasourceRequest);
        datasourceRequest.setQuery(ddlProvider.createView(dorisTableName, customSql));
        jdbcProvider.exec(datasourceRequest);
    }

    private void deleteDorisTable(String datasetId, DatasetTable table) throws Exception {
        String dorisTableName = TableUtils.dorisName(datasetId);
        Datasource dorisDatasource = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
        JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(dorisDatasource);
        DDLProvider ddlProvider = ProviderFactory.getDDLProvider(dorisDatasource.getType());
        if (StringUtils.equalsIgnoreCase("custom", table.getType())
                || StringUtils.equalsIgnoreCase("union", table.getType())) {
            datasourceRequest.setQuery(ddlProvider.dropView(dorisTableName));
            jdbcProvider.exec(datasourceRequest);
            datasourceRequest.setQuery(ddlProvider.dropView(TableUtils.dorisTmpName(dorisTableName)));
            jdbcProvider.exec(datasourceRequest);
        } else {
            datasourceRequest.setQuery(ddlProvider.dropTable(dorisTableName));
            jdbcProvider.exec(datasourceRequest);
            datasourceRequest.setQuery(ddlProvider.dropTable(TableUtils.dorisTmpName(dorisTableName)));
            jdbcProvider.exec(datasourceRequest);
        }
    }

    public Boolean checkDorisTableIsExists(String id) throws Exception {
        Datasource dorisDatasource = (Datasource) CommonBeanFactory.getBean("DorisDatasource");
        JdbcProvider jdbcProvider = CommonBeanFactory.getBean(JdbcProvider.class);
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(dorisDatasource);
        QueryProvider qp = ProviderFactory.getQueryProvider(dorisDatasource.getType());
        datasourceRequest.setQuery(qp.searchTable(TableUtils.dorisName(id)));
        List<String[]> data = jdbcProvider.getData(datasourceRequest);
        return CollectionUtils.isNotEmpty(data);
    }

    /**
     * 校验名称
     *
     * @param datasetTable
     */
    private void checkName(DatasetTable datasetTable) {
        QueryWrapper<DatasetTable> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(datasetTable.getId())) {
            queryWrapper.ne("id", datasetTable.getId());
        }
        if (StringUtils.isNotEmpty(datasetTable.getSceneId())) {
            queryWrapper.eq("scene_id", datasetTable.getSceneId());
        }
        if (StringUtils.isNotEmpty(datasetTable.getName())) {
            queryWrapper.eq("name", datasetTable.getName());
        }
        if (CollectionUtils.isNotEmpty(datasetTableMapper.selectList(queryWrapper))) {
            throw new RuntimeException(ResultCode.get("name_cant_repeat_same_group"));
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

    private String subPrefixSuffixChar(String str) {
        while (StringUtils.startsWith(str, ",")) {
            str = str.substring(1, str.length());
        }
        while (StringUtils.endsWith(str, ",")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 字段类型
     *
     * @param field
     * @return
     */
    public Integer transFieldType(String field) {
        switch (field) {
            case "TEXT":
                return 0;
            case "DATETIME":
                return 1;
            case "LONG":
            case "INT":
                return 2;
            case "DOUBLE":
                return 3;
            default:
                return 0;
        }
    }

    /*
     * 判断数组中是否有重复的值
     */
    public static boolean checkIsRepeat(String[] array) {
        HashSet<String> hashSet = new HashSet<String>();
        for (int i = 0; i < array.length; i++) {
            if (StringUtils.isEmpty(array[i])) {
                throw new RuntimeException(ResultCode.get("excel_empty_column"));
            }
            hashSet.add(array[i]);
        }
        if (hashSet.size() == array.length) {
            return false;
        } else {
            return true;
        }
    }
}
