package com.stdc.visual.dynamic.service.impl;

import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.TreeUtils;
import com.stdc.visual.dynamic.base.model.dto.ModelDTO;
import com.stdc.visual.dynamic.base.model.request.ModelRequest;
import com.stdc.visual.dynamic.mapper.ModelMapper;
import com.stdc.visual.dynamic.service.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/19--16:00
 * @describe: 数据集模型
 */
@Service
public class ModelServiceImpl implements ModelService {

    @Resource
    private ModelMapper extVAuthModelMapper;

    @Autowired
    private RoleSourceService roleSourceService;

    public List<ModelDTO> queryModel(ModelRequest request) {

        // 定时任务选数据集时，列表需去除空目录
        if (request.isClearEmptyDir()) {
            request.setMode(null);
        }
        List<ModelDTO> result = extVAuthModelMapper.queryModel(request);

        //权限筛选
        List<String> idS = new ArrayList<>();
        List<String> dataSetIdS = roleSourceService.queryCurrentRoleSourceId(SourceType.DATA_SET);
        List<String> dataSetGroupIdS = roleSourceService.queryCurrentRoleSourceId(SourceType.DATA_SET_GROUP);
        idS.addAll(dataSetIdS);
        idS.addAll(dataSetGroupIdS);
        if (CollectionUtils.isEmpty(idS)){
            return null;
        }
        result = result.stream().filter(modelDTO -> idS.contains(modelDTO.getId())).collect(Collectors.toList());

        // 定时任务选数据集时，列表需去除空目录
        if (request.isClearEmptyDir()) {
            result = filterData(request, result);
            List<ModelDTO> vAuthModelDTOS = TreeUtils.mergeTree(result);
            setAllLeafs(vAuthModelDTOS);
            removeEmptyDir(vAuthModelDTOS);
            return sort(vAuthModelDTOS);
        }
        return sort(TreeUtils.mergeTree(result));
    }

    private List<ModelDTO> filterData(ModelRequest request, List<ModelDTO> result) {
        if (request.getDatasetMode() != null && request.getDatasetMode() == 1) {
            result = result.stream().filter(vAuthModelDTO -> {
                if (vAuthModelDTO.getNodeType().equalsIgnoreCase("spine") || (vAuthModelDTO.getNodeType().equalsIgnoreCase("leaf") && vAuthModelDTO.getMode().equals(1L)) && !vAuthModelDTO.getModelInnerType().equalsIgnoreCase("excel") && !vAuthModelDTO.getModelInnerType().equalsIgnoreCase("custom") && !vAuthModelDTO.getModelInnerType().equalsIgnoreCase("union")) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
        }
        if (request.getPrivileges() != null) {
            result = result.stream().filter(vAuthModelDTO -> {
                if (vAuthModelDTO.getNodeType().equalsIgnoreCase("spine") || (vAuthModelDTO.getNodeType().equalsIgnoreCase("leaf") && vAuthModelDTO.getPrivileges() != null && vAuthModelDTO.getPrivileges().contains(request.getPrivileges()))) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
        }
        return result;
    }

    private void removeEmptyDir(List<ModelDTO> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        Iterator iterator = result.listIterator();
        while (iterator.hasNext()) {
            ModelDTO tmp = (ModelDTO) iterator.next();
            if (tmp.getNodeType().equalsIgnoreCase("spine") && tmp.getAllLeafs() == 0) {
                iterator.remove();
            } else {
                removeEmptyDir(tmp.getChildren());
            }
        }
    }

    private void setAllLeafs(List<ModelDTO> result) {
        for (ModelDTO vAuthModelDTO : result) {
            if (CollectionUtils.isEmpty(vAuthModelDTO.getChildren())) {
                vAuthModelDTO.setAllLeafs(0);
                continue;
            }
            long leafs = 0l;
            for (ModelDTO child : vAuthModelDTO.getChildren()) {
                if (child.getNodeType().equalsIgnoreCase("leaf")) {
                    leafs = leafs + 1;
                } else {
                    leafs = +leafs + getLeafs(child);
                }
            }
            vAuthModelDTO.setAllLeafs(leafs);
        }
    }

    private long getLeafs(ModelDTO child) {
        long leafs = 0l;
        if (CollectionUtils.isEmpty(child.getChildren())) {
            child.setAllLeafs(0);
            return leafs;
        }
        for (ModelDTO childChild : child.getChildren()) {
            if (childChild.getNodeType().equalsIgnoreCase("leaf")) {
                leafs = leafs + 1;
            } else {
                leafs = +leafs + getLeafs(childChild);
            }
        }
        child.setAllLeafs(leafs);
        return leafs;
    }

    private List<ModelDTO> sort(List<ModelDTO> result){
        result.forEach(r->{
            if (CollectionUtils.isNotEmpty(r.getChildren())){
                sort(r.getChildren());
                r.setChildren(r.getChildren().stream().sorted(Comparator.comparing(ModelDTO::getPid)).sorted(Comparator.comparing(ModelDTO::getName)).collect(Collectors.toList()));
            }
        });
        return result;
    }
}
