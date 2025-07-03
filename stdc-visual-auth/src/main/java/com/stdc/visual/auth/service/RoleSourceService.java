package com.stdc.visual.auth.service;

import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.role.po.RoleSourcePO;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/8/25--15:50
 * @describe: 角色 - 资源
 */
public interface RoleSourceService {

    Boolean saveRoleSource(Object sourceId, SourceType sourceType);

    List<RoleSourcePO> queryRoleSource(Object sourceId, SourceType sourceType);

    /**
     * 获取当用户 当前角色 ,资源类型的 资源id
     * @param sourceType
     * @return
     */
    List<String> queryCurrentRoleSourceId(SourceType sourceType);

    Boolean updateRoleSource(RoleSourcePO roleSource);

    Boolean delRoleSource(Object sourceId, SourceType sourceType);



}
