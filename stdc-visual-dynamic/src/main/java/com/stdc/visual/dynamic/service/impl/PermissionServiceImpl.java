package com.stdc.visual.dynamic.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.visual.auth.entity.permission.dto.*;
import com.stdc.visual.auth.entity.permission.po.ColumnPermissionPO;
import com.stdc.visual.auth.entity.permission.po.RowPermissionPO;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.mapper.SysUserMapper;
import com.stdc.visual.auth.service.*;
import com.stdc.visual.dynamic.base.chart.dto.ChartCustomFilterItemDTO;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableFieldsService;
import com.stdc.visual.dynamic.service.PermissionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/30--19:09
 * @describe: 权限操作类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private ColumnPermissionService columnPermissionService;

    @Autowired
    RowPermissionService rowPermissionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Resource
    private DataSetTableFieldsService dataSetTableFieldsService;

    @Autowired
    private AuthProperties authProperties;
    /**
     * 列权限
     * @param fields
     * @param desensitizationList
     * @param datasetTableId
     * @return
     */
    public List<DatasetTableField> filterColumnPermissions(List<DatasetTableField> fields, List<String> desensitizationList, String datasetTableId) {
        //权限关闭
        if (!authProperties.getEnable()){
            return fields;
        }
        List<DatasetTableField> result = new ArrayList<>();
        //当前账号 在 用户、角色、组织 层面的列权限
        List<ColumnPermissionFilterItem> allColumnPermissionItems = new ArrayList<>();
        for (ColumnPermissionPO po : columnPermissions(datasetTableId)) {
            //没有绑定数据集
            if (StringUtils.isEmpty(po.getDatasetId())) {
                continue;
            }
            ColumnPermissionFilterDTO dto = JSONObject.parseObject(po.getPermissions()).toJavaObject(ColumnPermissionFilterDTO.class);
            //列权限未开启
            if (!dto.getEnable()){
                continue;
            }
            //所有列权限 (选中状态)selected
            allColumnPermissionItems.addAll(dto.getColumns().stream().filter(column -> column.getSelected()).collect(Collectors.toList()));
        }
        //遍历当前数据集字段，将可访问的字段筛选出来
        fields.forEach(field->{
            List<String> permissions = allColumnPermissionItems.stream().filter(columnPermissionItem -> columnPermissionItem.getId().equalsIgnoreCase(field.getId())).map(ColumnPermissionFilterItem::getOpt).collect(Collectors.toList());
            //字段上无任何权限，默认返回所有
            if(CollectionUtils.isEmpty(permissions)){
                result.add(field);
            }
            //有权限
            else {
                //判断禁用权限(禁用权限优先于脱敏权限)
                if(!permissions.contains(ColumnPermissionConstants.Prohibit.getValue())){
                    //数据未禁用
                    result.add(field);
                    // 判断 脱敏权限
                    if(desensitizationList != null && permissions.contains(ColumnPermissionConstants.Desensitization.getValue())){
                        desensitizationList.add(field.getDataeaseName());
                    }
                }
			}
        });
        return result;
    }

    /**
     * 列权限
     * @param datasetId
     * @return
     */
    private List<ColumnPermissionPO> columnPermissions(String datasetId){
        List<ColumnPermissionPO> columnPermissionPOS = new ArrayList<>();
        SysUserDTO user = AuthUtils.getUser();
        //admin用户，查看所有权限
        if (user.isAdmin()) {
            return columnPermissionPOS;
        }
        String userId = user.getUserId();
        String deptId  = user.getDeptId();
        List<String> roleIds = user.getRoles().stream().map(RoleDTO::getRoleId).collect(Collectors.toList());
        ColumnPermissionDTO dto = new ColumnPermissionDTO();
        dto.setDatasetId(datasetId);
        //查询账号用户 列权限
        dto.setAuthTargetType("user");
        dto.setAuthTargetId(userId);
        columnPermissionPOS.addAll(columnPermissionService.query(dto));
        //查询账号角色 列权限
        dto.setAuthTargetType("role");
        for (String roleId : roleIds) {
            dto.setAuthTargetId(roleId);
            columnPermissionPOS.addAll(columnPermissionService.query(dto));
        }
        //查询账号组织 列权限
        dto.setAuthTargetType("dept");
        dto.setAuthTargetId(deptId);
        columnPermissionPOS.addAll(columnPermissionService.query(dto));
        //返回当前账号 在 用户、角色、组织 层面的列权限
        return columnPermissionPOS;
    }


    /**
     * 行权限
     * @param fields
     * @param datasetTable
     * @return
     */
    public List<ChartFieldCustomFilterDTO> filterRowPermissions(DataSetTableRequest dataSetTableRequest, List<DatasetTableField> fields, DatasetTable datasetTable) {
        List<ChartFieldCustomFilterDTO> rowFilterS = new ArrayList<>();
        //权限关闭
        if (!authProperties.getEnable()){
            return rowFilterS;
        }
        for (RowPermissionPO po : rowPermissions(datasetTable.getId())) {
            //没有绑定数据字段
            if (StringUtils.isEmpty(po.getDatasetFieldId())) {
                continue;
            }
            //筛选行权限之前，先进行列权限筛选，查看是否有权限查看列
            DatasetTableField checkField = getFieldById(fields, po.getDatasetFieldId());
            if (checkField == null) {
                continue;
            }
            //定义where条件
            ChartFieldCustomFilterDTO whereFilterDTO = new ChartFieldCustomFilterDTO();
            //设置字段详情
            DatasetTableField field = dataSetTableFieldsService.selectByPrimaryKey(po.getDatasetFieldId());
            whereFilterDTO.setField(field);
            //设置逻辑符
            whereFilterDTO.setLogic(po.getLogic());
            //设置where条件
            //判断筛选值为自定义还是枚举值，枚举值逻辑关系为and，筛选符为eq
            List<ChartCustomFilterItemDTO> filterS = new ArrayList<>();
            switch (po.getFilterType()){
                case "enum":
                    ChartCustomFilterItemDTO d = new ChartCustomFilterItemDTO();
                    d.setFieldId(po.getDatasetFieldId());
                    d.setTerm("in");
                    d.setValue(po.getEnumCheckField());
                    filterS.add(d);
                    break;
                case "logic":
                    JSONArray.parseArray(po.getFilter()).forEach(filter -> {
                        RowPermissionFilterDTO rowFilter = ((JSONObject) filter).toJavaObject(RowPermissionFilterDTO.class);
                        ChartCustomFilterItemDTO f = new ChartCustomFilterItemDTO();
                        f.setFieldId(po.getDatasetFieldId());
                        f.setTerm(rowFilter.getTerm());
                        f.setValue(rowFilter.getValue());
                        filterS.add(f);
                    });
                    break;
                default:
                    break;
            }
            whereFilterDTO.setFilter(filterS);
            whereFilterDTO.setSuperRequest(dataSetTableRequest);
            rowFilterS.add(whereFilterDTO);
        }
        return rowFilterS;
    }

    /**
     * 获取行权限列表
     * @param datasetId
     * * @return
     */
    private List<RowPermissionPO> rowPermissions(String datasetId) {
        List<RowPermissionPO> rowPermissionPOS = new ArrayList<>();
        SysUserDTO user = AuthUtils.getUser();
        //admin用户，查看所有权限
        if (user.isAdmin()) {
            return rowPermissionPOS;
        }
        String userId = user.getUserId();
        String deptId  = user.getDeptId();
        List<String> roleIds = user.getRoles().stream().map(RoleDTO::getRoleId).collect(Collectors.toList());
        //筛选 查询行权限
        RowPermissionDTO dto = new RowPermissionDTO();
        dto.setDatasetId(datasetId);
        //查询账号用户 行权限
        dto.setAuthTargetType(AuthTargetType.USER.getType());
        dto.setAuthTargetId(userId);
        rowPermissionPOS.addAll(rowPermissionService.query(dto));
        //查询账号角色 行权限
        dto.setAuthTargetType(AuthTargetType.ROLE.getType());
        for (String roleId : roleIds) {
            dto.setAuthTargetId(roleId);
            rowPermissionPOS.addAll(rowPermissionService.query(dto));
        }
        //查询账号组织 行权限
        dto.setAuthTargetType(AuthTargetType.DEPT.getType());
        dto.setAuthTargetId(deptId);
        rowPermissionPOS.addAll(rowPermissionService.query(dto));
        //返回当前账号 在 用户、角色、组织 层面的行权限
        return rowPermissionPOS;
    }
    /**
     * 删选行权限之前，先进行列权限筛选，查看是否有权限查看列
     */
    private DatasetTableField getFieldById(List<DatasetTableField> fields, String fieldId) {
        DatasetTableField field = null;
        for (DatasetTableField datasetTableField : fields) {
            if (fieldId.equalsIgnoreCase(datasetTableField.getId())) {
                field = datasetTableField;
            }
        }
        return field;
    }

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUserDTO user) {
        Set<String> roles = new HashSet<>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            roles.add("admin");
        } else {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }


    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUserDTO user) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (user.isAdmin()) {
            perms.add("*:*:*");
        } else {
            List<RoleDTO> roles = user.getRoles();
            if (!org.springframework.util.CollectionUtils.isEmpty(roles)) {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (RoleDTO role : roles) {
                    /*if (StringUtil.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && !role.isAdmin()) {
                        Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                        role.setPermissions(rolePerms);
                        perms.addAll(rolePerms);
                    }*/
                }
            } else {
                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
            }
        }
        return perms;
    }

}
