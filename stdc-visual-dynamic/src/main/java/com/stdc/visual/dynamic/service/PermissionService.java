package com.stdc.visual.dynamic.service;

import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.dynamic.base.chart.dto.ChartFieldCustomFilterDTO;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTableField;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;

import java.util.List;
import java.util.Set;

/**
 * @author: wang_jie
 * @data: 2022/5/30--19:06
 * @describe: 权限操作类
 */
public interface PermissionService {

    /**
     * 列权限
     * @param fields
     * @param desensitizationList
     * @param datasetTableId
     * @return
     */
    List<DatasetTableField> filterColumnPermissions(List<DatasetTableField> fields, List<String> desensitizationList, String datasetTableId);

    /**
     * 行权限
     * @param dataSetTableRequest
     * @param fields
     * @param datasetTable
     * @return
     */
    List<ChartFieldCustomFilterDTO> filterRowPermissions(DataSetTableRequest dataSetTableRequest, List<DatasetTableField> fields, DatasetTable datasetTable);

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUserDTO user);

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUserDTO user);



}
