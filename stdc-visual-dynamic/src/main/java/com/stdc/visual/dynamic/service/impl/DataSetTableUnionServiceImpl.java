package com.stdc.visual.dynamic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableUnionDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableUnion;
import com.stdc.visual.dynamic.mapper.DatasetTableUnionMapper;
import com.stdc.visual.dynamic.service.DataSetTableUnionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/17--15:26
 * @describe:
 */
@Service
public class DataSetTableUnionServiceImpl implements DataSetTableUnionService {

    @Resource
    private DatasetTableUnionMapper datasetTableUnionMapper;

    @Override
    public DatasetTableUnion save(DatasetTableUnion datasetTableUnion) {
        checkUnion(datasetTableUnion);
        if (StringUtils.isEmpty(datasetTableUnion.getId())) {
            datasetTableUnion.setId(UUID.randomUUID().toString());
            //datasetTableUnion.setCreateBy(AuthUtils.getUser().getUsername());
            datasetTableUnion.setCreateTime(System.currentTimeMillis());
            datasetTableUnionMapper.insert(datasetTableUnion);
        } else {
            datasetTableUnionMapper.updateById(datasetTableUnion);
        }
        return datasetTableUnion;
    }

    @Override
    public void delete(String id) {
        datasetTableUnionMapper.deleteById(id);
    }

    @Override
    public List<DataSetTableUnionDTO> listByTableId(String tableId) {
        List<DataSetTableUnionDTO> sourceList = datasetTableUnionMapper.selectBySourceTableId(tableId);
        List<DataSetTableUnionDTO> targetList = datasetTableUnionMapper.selectByTargetTableId(tableId);
        sourceList.addAll(targetList.stream().map(ele -> {
            DataSetTableUnionDTO dto = new DataSetTableUnionDTO();
            dto.setId(ele.getId());

            dto.setSourceTableId(ele.getTargetTableId());
            dto.setSourceTableFieldId(ele.getTargetTableFieldId());
            dto.setSourceTableName(ele.getTargetTableName());
            dto.setSourceTableFieldName(ele.getTargetTableFieldName());

            dto.setTargetTableId(ele.getSourceTableId());
            dto.setTargetTableFieldId(ele.getSourceTableFieldId());
            dto.setTargetTableName(ele.getSourceTableName());
            dto.setTargetTableFieldName(ele.getSourceTableFieldName());

            dto.setSourceUnionRelation(ele.getTargetUnionRelation());
            dto.setTargetUnionRelation(ele.getSourceUnionRelation());

            dto.setCreateBy(ele.getCreateBy());
            dto.setCreateTime(ele.getCreateTime());
            return dto;
        }).collect(Collectors.toList()));

        sourceList.sort(Comparator.comparing(DatasetTableUnion::getCreateTime));
        return sourceList;
    }

    @Override
    public void deleteUnionByTableId(String tableId) {
        datasetTableUnionMapper.delete(new QueryWrapper<DatasetTableUnion>()
                .eq("source_table_id", tableId)
                .or()
                .eq("target_table_id", tableId));
    }

    /**
     * check 关联关系是否存在
     *
     * @param datasetTableUnion
     */
    public void checkUnion(DatasetTableUnion datasetTableUnion) {
        // check 关联关系是否存在
        QueryWrapper<DatasetTableUnion> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(datasetTableUnion.getId())) {
            queryWrapper.ne("id", datasetTableUnion.getId());
        }
        queryWrapper.eq("source_table_id", datasetTableUnion.getSourceTableId());
        queryWrapper.eq("source_table_field_id", datasetTableUnion.getSourceTableFieldId());
        queryWrapper.eq("target_table_id", datasetTableUnion.getTargetTableId());
        queryWrapper.eq("target_table_field_id", datasetTableUnion.getTargetTableFieldId());
        List<DatasetTableUnion> sourceResult = datasetTableUnionMapper.selectList(queryWrapper);
        queryWrapper.clear();
        //source 和 target 条件互换
        if (StringUtils.isNotEmpty(datasetTableUnion.getId())) {
            queryWrapper.ne("id", datasetTableUnion.getId());
        }
        queryWrapper.eq("source_table_id", datasetTableUnion.getTargetTableId());
        queryWrapper.eq("source_table_field_id", datasetTableUnion.getTargetTableFieldId());
        queryWrapper.eq("target_table_id", datasetTableUnion.getSourceTableId());
        queryWrapper.eq("target_table_field_id", datasetTableUnion.getSourceTableFieldId());
        List<DatasetTableUnion> targetResult = datasetTableUnionMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(sourceResult) || CollectionUtils.isNotEmpty(targetResult)) {
            throw new RuntimeException(ResultCode.get("union_already_exists"));
        }
        // check 同一字段是否在两个关联表中重复出现
        List<DataSetTableUnionDTO> sourceResult1 = datasetTableUnionMapper.selectUsedFieldBySource(datasetTableUnion);
        List<DataSetTableUnionDTO> targetResult1 = datasetTableUnionMapper.selectUsedFieldByTarget(datasetTableUnion);
        if (CollectionUtils.isNotEmpty(sourceResult1) || CollectionUtils.isNotEmpty(targetResult1)) {
            throw new RuntimeException(ResultCode.get("union_field_exists"));
        }
    }
}
