package com.stdc.visual.dynamic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.common.utils.TreeUtils;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetGroupDTO;
import com.stdc.visual.dynamic.base.dataset.dto.DataSetTableDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetGroup;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetGroupRequest;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.mapper.DatasetGroupMapper;
import com.stdc.visual.dynamic.mapper.DatasetTableMapper;
import com.stdc.visual.dynamic.service.DataSetGroupService;
import com.stdc.visual.dynamic.service.DataSetTableService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/17--11:33
 * @describe:
 */
@Service
public class DataSetGroupServiceImpl implements DataSetGroupService {

    @Autowired
    private DatasetGroupMapper datasetGroupMapper;

    @Autowired
    private DatasetTableMapper datasetTableMapper;

    @Autowired
    private DataSetTableService dataSetTableService;

    @Autowired
    private RoleSourceService roleSourceService;

    @Override
    public DataSetGroupDTO save(DatasetGroup datasetGroup) {
        checkName(datasetGroup);
        if (StringUtils.isEmpty(datasetGroup.getId())) {
            String id = StringUtil.randomUUID();
            datasetGroup.setId(id);
            datasetGroup.setCreateBy(AuthUtils.getUser().getUsername());
            datasetGroup.setCreateTime(System.currentTimeMillis());
            datasetGroupMapper.insert(datasetGroup);
            roleSourceService.saveRoleSource(id, SourceType.DATA_SET_GROUP);
        } else {
            datasetGroupMapper.updateById(datasetGroup);
        }
        DataSetGroupDTO dataSetGroupDTO = new DataSetGroupDTO();
        BeanUtils.copyBean(dataSetGroupDTO, datasetGroup);
        dataSetGroupDTO.setLabel(dataSetGroupDTO.getName());
        return dataSetGroupDTO;
    }

    @Override
    public void delete(String id) throws Exception {
        Assert.notNull(id, "id cannot be null");
        DatasetGroup dg = datasetGroupMapper.selectById(id);
        DataSetGroupRequest datasetGroup = new DataSetGroupRequest();
        BeanUtils.copyBean(datasetGroup, dg);
        List<String> idS = datasetTableMapper.selectList(new QueryWrapper<DatasetTable>().eq("scene_id", id))
                .stream().map(DatasetTable::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idS)){
            idS.forEach(tableId-> dataSetTableService.delete(tableId));
        }
        datasetGroupMapper.deleteById(datasetGroup);
    }

    @Override
    public List<DataSetGroupDTO> tree(DataSetGroupRequest datasetGroup) {
        datasetGroup.setLevel(null);
        datasetGroup.setPid(null);
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.DATA_SET_GROUP);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        datasetGroup.setIds(new HashSet<>(ids));
        List<DataSetGroupDTO> search = datasetGroupMapper.search(datasetGroup);
        return TreeUtils.mergeTree(search);
    }

    @Override
    public List<DataSetGroupDTO> treeNode(DataSetGroupRequest datasetGroup) {
        return null;
    }


    /**
     * 检验数据集分组名称
     *
     * @param datasetGroup
     */
    private void checkName(DatasetGroup datasetGroup) {
        QueryWrapper<DatasetGroup> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(datasetGroup.getPid())) {
            queryWrapper.eq("pid", datasetGroup.getPid());
        }
        if (StringUtils.isNotEmpty(datasetGroup.getName())) {
            queryWrapper.eq("name", datasetGroup.getName());
        }
        List<DatasetGroup> datasetGroups = datasetGroupMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(datasetGroups)) {
            BaseException.throwException(ResultCode.get("name_cant_repeat_same_group"));
        }
    }

    // 删除场景下的表和字段
    public void deleteTableAndField(List<String> sceneIds) throws Exception {
        for (String sceneId : sceneIds) {
            DataSetTableRequest dataSetTableRequest = new DataSetTableRequest();
            dataSetTableRequest.setSceneId(sceneId);
            List<DataSetTableDTO> list = dataSetTableService.list(dataSetTableRequest);
            for (DataSetTableDTO table : list) {
                dataSetTableService.delete(table.getId());
            }
        }
    }

    @Override
    public List<DatasetGroup> getParents(String id) {
        List<DatasetGroup> list = new ArrayList<>();
        DatasetGroup datasetGroup = datasetGroupMapper.selectById(id);
        list.add(datasetGroup);
        getParent(list, datasetGroup);
        Collections.reverse(list);
        return list;
    }

    @Override
    public void getParent(List<DatasetGroup> list, DatasetGroup datasetGroup) {
        if (ObjectUtils.isNotEmpty(datasetGroup)) {
            if (StringUtils.isNotEmpty(datasetGroup.getPid())) {
                DatasetGroup d = datasetGroupMapper.selectById(datasetGroup.getPid());
                list.add(d);
                getParent(list, d);
            }
        }
    }

    @Override
    public DatasetGroup getScene(String id) {
        return datasetGroupMapper.selectById(id);
    }
}
