package com.stdc.visual.dynamic.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.dynamic.base.dataset.dto.DataTableInfoDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.datasource.dto.DBTableDTO;
import com.stdc.visual.dynamic.base.datasource.dto.DatasourceTypes;
import com.stdc.visual.dynamic.base.datasource.dto.TableDesc;
import com.stdc.visual.dynamic.base.datasource.dto.configuration.*;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.base.datasource.request.DatasourceRequest;
import com.stdc.visual.dynamic.mapper.DatasetTableMapper;
import com.stdc.visual.dynamic.mapper.DatasourceMapper;
import com.stdc.visual.dynamic.provider.ProviderFactory;
import com.stdc.visual.dynamic.provider.datasource.DatasourceProvider;
import com.stdc.visual.dynamic.service.DataSetGroupService;
import com.stdc.visual.dynamic.service.DatasourceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--9:33
 * @describe:
 */
@Service
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Autowired
    private CommonThreadPool commonThreadPool;

    @Resource
    private DatasetTableMapper datasetTableMapper;

    @Resource
    private DataSetGroupService dataSetGroupService;

    @Autowired
    private RoleSourceService roleSourceService;

    @Override
    public Datasource addDatasource(Datasource datasource) {
        checkName(datasource);
        long currentTimeMillis = System.currentTimeMillis();
        String id = StringUtil.randomUUID();
        datasource.setId(id);
        datasource.setUpdateTime(currentTimeMillis);
        datasource.setCreateTime(currentTimeMillis);
        datasource.setCreateBy(String.valueOf(AuthUtils.getUser().getUsername()));
        checkAndUpdateDatasourceStatus(datasource);
        datasourceMapper.insertSelective(datasource);
        roleSourceService.saveRoleSource(id, SourceType.DATASOURCE);
        handleConnectionPool(datasource, "add");
        return datasource;
    }

    @Override
    public List<Datasource> getDatasourceList() {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.DATASOURCE);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        List<Datasource> datasourceS = datasourceMapper.selectList(new LambdaQueryWrapper<Datasource>().in(Datasource::getId,ids));
        datasourceS.forEach(d -> {
            DatasourceTypes datasourceType = DatasourceTypes.valueOf(d.getType());
            try {
                switch (datasourceType) {
                    case tdengine:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), TdengineConfiguration.class)));
                        break;
                    case mysql:
                    case mariadb:
                    case de_doris:
                    case ds_doris:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), MysqlConfiguration.class)));
                        break;
                    case gbase:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), GbaseConfiguration.class)));
                        break;
                    case dm:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), DMConfiguration.class)) );
                    case sqlServer:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), SqlServerConfiguration.class)));
                        break;
                    case oracle:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), OracleConfiguration.class)));
                        break;
                    case pg:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), PgConfiguration.class)));
                        break;
                    case ck:
                        d.setConfiguration(JSONObject.toJSONString(JsonUtil.toJavaObj(d.getConfiguration(), CHConfiguration.class)));
                        break;
                    default:
                        break;
                }
            } catch (Exception ignore) {
            }
        });
        //数据源列表按照数据源列表按照类型名称和数据源名称升序排序，忽略大小写
        datasourceS.sort((o1,o2) -> StringUtils.compareIgnoreCase(o1.getName(), o2.getName()));
        return datasourceS;
    }

    @Override
    public R deleteDatasource(String datasourceId) {
        List<DatasetTable> datasetTables = datasetTableMapper.selectList(new QueryWrapper<DatasetTable>().eq("data_source_id", datasourceId));
        if (CollectionUtils.isNotEmpty(datasetTables)) {
            BaseException.throwException(datasetTables.size() + ResultCode.get("datasource_not_allow_delete_msg"));
        }
        Datasource datasource = datasourceMapper.selectById(datasourceId);
        int i = datasourceMapper.deleteById(datasource);
        if (i <= 0){
            BaseException.throwException(ResultCode.get("ds_not_exists"));
        }
        handleConnectionPool(datasource, "delete");
        return R.success("true");
    }

    @Override
    public void updateDatasource(Datasource datasource) {
        checkName(datasource);
        datasource.setCreateTime(null);
        datasource.setUpdateTime(System.currentTimeMillis());
        checkAndUpdateDatasourceStatus(datasource);
        datasourceMapper.updateById(datasource);
        handleConnectionPool(datasource, "edit");
    }

    @Override
    public R validate(Datasource datasource) {
        try {
            DatasourceProvider datasourceProvider = ProviderFactory.getProvider(datasource.getType());
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setDatasource(datasource);
            datasourceProvider.checkStatus(datasourceRequest);
            return R.success("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("Datasource is invalid: " + e.getMessage());
        }
    }

    @Override
    public R validate(String datasourceId) {
        Datasource datasource = datasourceMapper.selectById(datasourceId);
        if (datasource == null) {
            return R.fail("Can not find datasource: " + datasourceId);
        }
        return validate(datasource);
    }

    @Override
    public List<DBTableDTO> getTables(Datasource datasource) throws Exception {
        Datasource ds = datasourceMapper.selectById(datasource.getId());
        DatasourceProvider datasourceProvider = ProviderFactory.getProvider(ds.getType());
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(ds);
        datasourceProvider.checkStatus(datasourceRequest);
        List<TableDesc> tables = datasourceProvider.getTables(datasourceRequest);

        // 获取当前数据源下的db类型数据集
        List<DatasetTable> datasetTables = datasetTableMapper.selectList(new QueryWrapper<DatasetTable>().eq("type", "db").eq("data_source_id", datasource.getId()));
        List<DBTableDTO> list = new ArrayList<>();
        for (TableDesc tableDesc : tables) {
            DBTableDTO dbTableDTO = new DBTableDTO();
            dbTableDTO.setDatasourceId(datasource.getId());
            dbTableDTO.setName(tableDesc.getName());
            dbTableDTO.setRemark(tableDesc.getRemark());
            dbTableDTO.setEnableCheck(true);
            dbTableDTO.setDatasetPath(null);
            for (DatasetTable datasetTable : datasetTables) {
                DataTableInfoDTO dataTableInfoDTO = JsonUtil.toJavaObj(datasetTable.getInfo(), DataTableInfoDTO.class);
                if (StringUtils.equals(tableDesc.getName(), dataTableInfoDTO.getTable())) {
                    dbTableDTO.setEnableCheck(false);
                    List<DatasetGroup> parents = dataSetGroupService.getParents(datasetTable.getSceneId());
                    StringBuilder stringBuilder = new StringBuilder();
                    parents.forEach(ele -> {
                        if (ObjectUtils.isNotEmpty(ele)) {
                            stringBuilder.append(ele.getName()).append("/");
                        }
                    });
                    stringBuilder.append(datasetTable.getName());
                    dbTableDTO.setDatasetPath(stringBuilder.toString());
                    break;
                }
            }
            list.add(dbTableDTO);
        }
        return list;
    }

    @Override
    public Datasource get(String id) {
        return datasourceMapper.selectById(id);
    }

    @Override
    public List<String> getSchema(Datasource datasource) {
        List<String> schema = null;
        DatasourceProvider datasourceProvider = ProviderFactory.getProvider(datasource.getType());
        DatasourceRequest datasourceRequest = new DatasourceRequest();
        datasourceRequest.setDatasource(datasource);
        try {
             schema = datasourceProvider.getSchema(datasourceRequest);
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("datasource_getSchema_error") + e.getMessage());
        }
        return schema;
    }

    /**
     * 检验数据源名称是否重复
     *
     * @param datasource
     */
    private void checkName(Datasource datasource) {
        QueryWrapper<Datasource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", datasource.getName());
        if (StringUtils.isNotEmpty(datasource.getId())) {
            queryWrapper.ne("id", datasource.getId());
        }
        List<Datasource> datasourceS = datasourceMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(datasourceS)) {
            BaseException.throwException(ResultCode.get("ds_name_exists"));
        }
    }

    /**
     * 添加数据源连接到 mysql 连接池
     *
     * @param datasource
     * @param type
     */
    private void handleConnectionPool(Datasource datasource, String type) {
        commonThreadPool.addTask(() -> {
            try {
                DatasourceProvider datasourceProvider = ProviderFactory.getProvider(datasource.getType());
                DatasourceRequest datasourceRequest = new DatasourceRequest();
                datasourceRequest.setDatasource(datasource);
                datasourceProvider.handleDatasource(datasourceRequest, type);
                LogUtil.info("Succsss to {} datasource connection pool: {}", type, datasource.getName());
            } catch (Exception e) {
                LogUtil.error("Failed to handle datasource connection pool: " + datasource.getName(), e);
            }
        });
    }

    /**
     * 检验并且更新数据源可用状态
     *
     * @param datasource
     */
    private void checkAndUpdateDatasourceStatus(Datasource datasource) {
        try {
            DatasourceProvider datasourceProvider = ProviderFactory.getProvider(datasource.getType());
            DatasourceRequest datasourceRequest = new DatasourceRequest();
            datasourceRequest.setDatasource(datasource);
            datasourceProvider.checkStatus(datasourceRequest);
            datasource.setStatus("Success");
        } catch (Exception e) {
            datasource.setStatus("Error");
        }
    }
}
