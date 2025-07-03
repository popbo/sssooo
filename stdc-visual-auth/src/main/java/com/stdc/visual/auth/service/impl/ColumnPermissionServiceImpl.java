package com.stdc.visual.auth.service.impl;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.permission.dto.AuthTargetType;
import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionDTO;
import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionFilterDTO;
import com.stdc.visual.auth.entity.permission.dto.ColumnPermissionFilterItem;
import com.stdc.visual.auth.entity.permission.po.ColumnPermissionPO;
import com.stdc.visual.auth.entity.permission.vo.ColumnPermissionVO;
import com.stdc.visual.auth.mapper.ColumnPermissionMapper;
import com.stdc.visual.auth.service.ColumnPermissionService;
import com.stdc.visual.auth.service.DeptService;
import com.stdc.visual.auth.service.RoleService;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.common.utils.JsonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/30--18:53
 * @describe: 列权限操作实现类
 */
@Service
public class ColumnPermissionServiceImpl implements ColumnPermissionService {

    @Autowired
    private ColumnPermissionMapper columnMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;

    @Override
    public Boolean save(ColumnPermissionDTO dto) {
        if (!checkColumn(dto)) {
            BaseException.throwException(ResultCode.get("column_permission_exit"));
        }
        dto.setId(StringUtil.randomUUID());
        dto.setUpdateTime(System.currentTimeMillis());
        int save = columnMapper.save(dto);
        return save > 0 ? true : false;
    }

    @Override
    public Boolean deleteById(String id) {
        int del = columnMapper.deleteByPrimaryKey(id);
        return del > 0 ? true : false ;
    }

    @Override
    public Boolean update(ColumnPermissionDTO dto) {
        dto.setUpdateTime(System.currentTimeMillis());
        int update = columnMapper.update(dto);
        return update > 0 ? true : false ;
    }

    @Override
    public List<ColumnPermissionVO> query(ColumnPermissionDTO request) {
        List<ColumnPermissionVO> vos = new ArrayList<>();
        columnMapper.query(request).forEach(q->{
            ColumnPermissionVO v = new ColumnPermissionVO();
            BeanUtils.copyProperties(q,v);
            v.setTypeName(AuthTargetType.getNameByType(v.getAuthTargetType()));
            switch (q.getAuthTargetType()){
                case "user":
                    v.setName(sysUserService.queryOneByUserId(String.valueOf(v.getAuthTargetId())).getUsername());
                    break;
                case "role":
                    v.setName(roleService.queryByRoleId(v.getAuthTargetId()).getName());
                    break;
                case "dept":
                    v.setName(deptService.queryDeptById(v.getAuthTargetId()).getName());
                    break;
            }
            vos.add(v);
        });
        return vos;
    }

    @Override
    public List<ColumnPermissionPO> allPermissionS() {
        return columnMapper.queryAll();
    }

    /**
     * 判断是否有已有列权限存在
     * @param dto
     * @return
     */
    private boolean checkColumn(ColumnPermissionDTO dto){
        List<String> permissionS = columnMapper.queryAllPermission();
        for (String permission : permissionS) {
            ColumnPermissionFilterDTO permissionFilter = JsonUtil.toJavaObj(permission,ColumnPermissionFilterDTO.class);
            if (!permissionFilter.getEnable() &&CollectionUtils.isEmpty(permissionFilter.getColumns())){
                continue;
            }
            ColumnPermissionFilterDTO requestFilter = JsonUtil.toJavaObj(dto.getPermissions(),ColumnPermissionFilterDTO.class);
            if (!requestFilter.getEnable() && CollectionUtils.isEmpty(requestFilter.getColumns())){
                continue;
            }
            for (ColumnPermissionFilterItem column : requestFilter.getColumns()) {
                for (ColumnPermissionFilterItem permissionFilterColumn : permissionFilter.getColumns()) {
                    if (StringUtil.equals(column.getId(),permissionFilterColumn.getId())){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
