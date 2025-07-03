package com.stdc.visual.dynamic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.visual.common.utils.TableUtils;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.mapper.DatasetTableFieldMapper;
import com.stdc.visual.dynamic.service.DataSetTableFieldsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/17--14:42
 * @describe:
 */
@Service("dataSetTableFieldsService")
public class DataSetTableFieldsServiceImpl implements DataSetTableFieldsService {

    @Resource
    private DatasetTableFieldMapper datasetTableFieldMapper;

    @Override
    public void batchEdit(List<DatasetTableField> list) {
        for (DatasetTableField field : list) {
            save(field);
        }
    }

    @Override
    public DatasetTableField save(DatasetTableField datasetTableField) {
        if (StringUtils.isEmpty(datasetTableField.getId())) {
            datasetTableField.setId(UUID.randomUUID().toString());
            // 若dataeasename为空，则用MD5(id)作为dataeasename
            if (StringUtils.isEmpty(datasetTableField.getDataeaseName())) {
                datasetTableField.setDataeaseName(TableUtils.columnName(datasetTableField.getId()));
            }
            if (ObjectUtils.isEmpty(datasetTableField.getLastSyncTime())) {
                datasetTableField.setLastSyncTime(System.currentTimeMillis());
            }
            datasetTableFieldMapper.insert(datasetTableField);
        } else {
            datasetTableFieldMapper.updateById(datasetTableField);
        }
        return datasetTableField;
    }

    @Override
    public List<DatasetTableField> list(DatasetTableField datasetTableField) {
        //默认不需要主键
        return list(datasetTableField, false);
    }

    @Override
    public List<DatasetTableField> list(DatasetTableField datasetTableField, Boolean primaryKey) {
        QueryWrapper<DatasetTableField> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(datasetTableField.getTableId())) {
            queryWrapper.eq("table_id", datasetTableField.getTableId());
        }
        if (ObjectUtils.isNotEmpty(datasetTableField.getChecked())) {
            queryWrapper.eq("checked", datasetTableField.getChecked());
        }
        if (ObjectUtils.isNotEmpty(datasetTableField.getGroupType())) {
            queryWrapper.eq("group_type", datasetTableField.getGroupType());
        }
        queryWrapper.orderByAsc("column_index");
        List<DatasetTableField> datasetTableFieldS = datasetTableFieldMapper.selectList(queryWrapper);
        //不需要主键
        if (!primaryKey) {
            return datasetTableFieldS.stream()
                    .filter(field -> !StringUtils.equalsIgnoreCase(field.getOriginName(), "id") || StringUtils.isNotBlank(field.getName()) ? !field.getName().equals("主键") : false)
                    .collect(Collectors.toList());
            //需要主键
        } else {
            return datasetTableFieldS;
        }
    }

    @Override
    public void deleteByTableId(String tableId) {
        datasetTableFieldMapper.delete(new QueryWrapper<DatasetTableField>().eq("table_id", tableId));
    }

    @Override
    public List<DatasetTableField> getListByIds(List<String> ids) {
        return datasetTableFieldMapper.selectList(new QueryWrapper<DatasetTableField>().in("id", ids));
    }

    @Override
    public DatasetTableField getOneById(String id) {
        return datasetTableFieldMapper.selectById(id);
    }

    @Override
    public DatasetTableField selectByPrimaryKey(String id) {
        return datasetTableFieldMapper.selectById(id);

    }

    @Override
    public DatasetTableField get(String id) {
        return datasetTableFieldMapper.selectById(id);
    }

    @Override
    public List<DatasetTableField> getListByIdsEach(List<String> ids) {
        return datasetTableFieldMapper.selectList(new QueryWrapper<DatasetTableField>().in("id", ids));
    }

    @Override
    public List<DatasetTableField> getFieldsByTableId(String id) {
        return datasetTableFieldMapper.selectList(new QueryWrapper<DatasetTableField>().eq("table_id", id));
    }

    @Override
    public void delete(String id) {
        datasetTableFieldMapper.deleteById(id);
    }

    @Override
    public void checkFieldName(DatasetTableField datasetTableField) {
        if (StringUtils.isNotEmpty(datasetTableField.getName()) && StringUtils.isNotEmpty(datasetTableField.getTableId())) {
            QueryWrapper<DatasetTableField> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",datasetTableField.getName()).eq("table_id",datasetTableField.getTableId());
            if (StringUtils.isNotEmpty(datasetTableField.getId())) {
                queryWrapper.ne("id",datasetTableField.getId());

            }
            List<DatasetTableField> datasetTableFields = datasetTableFieldMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(datasetTableFields)) {
                BaseException.throwException(ResultCode.get("field_name_repeat"));
            }
        }
    }
}
