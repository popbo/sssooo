package com.stdc.visual.auth.service.impl;

import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.permission.dto.AuthTargetType;
import com.stdc.visual.auth.entity.permission.dto.RowPermissionDTO;
import com.stdc.visual.auth.entity.permission.po.RowPermissionPO;
import com.stdc.visual.auth.entity.permission.vo.RowPermissionVO;
import com.stdc.visual.auth.mapper.RowPermissionMapper;
import com.stdc.visual.auth.service.DeptService;
import com.stdc.visual.auth.service.RoleService;
import com.stdc.visual.auth.service.RowPermissionService;
import com.stdc.visual.auth.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/30--18:54
 * @describe: 行权限操作实现类
 */
@Service
public class RowPermissionServiceImpl implements RowPermissionService {

    @Autowired
    private RowPermissionMapper rowMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleService roleService;


    @Override
    public Boolean save(RowPermissionDTO dto) {
        dto.setId(StringUtil.randomUUID());
        dto.setUpdateTime(System.currentTimeMillis());
        int save = rowMapper.save(dto);
        return save > 0 ? true : false;
    }

    @Override
    public Boolean deleteById(String id) {
        int del = rowMapper.deleteByPrimaryKey(id);
        return del > 0 ? true : false;
    }

    @Override
    public Boolean update(RowPermissionDTO dto) {
        dto.setUpdateTime(System.currentTimeMillis());
        int update = rowMapper.update(dto);
        return update > 0 ? true : false;
    }

    @Override
    public List<RowPermissionPO> query(RowPermissionDTO request) {
        return rowMapper.query(request);
    }
    @Override
    public List<RowPermissionVO> queryVO(RowPermissionDTO request) {
        List<RowPermissionVO> vos = new ArrayList<>();
        query(request).forEach(q->{
            RowPermissionVO v = new RowPermissionVO();
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
            v.setFieldName(rowMapper.queryFieldNameById(v.getDatasetFieldId()));
            vos.add(v);
        });
        return vos;
    }

    @Override
    public List<RowPermissionPO> allPermissionS() {
        return rowMapper.queryAll();
    }
}
