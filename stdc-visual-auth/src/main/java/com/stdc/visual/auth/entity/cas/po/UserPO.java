package com.stdc.visual.auth.entity.cas.po;

import lombok.Data;

/**
 * @author: wang_jie
 * @data: 2022/10/26--14:37
 * @describe: 统一认证-远程调用-用户信息实体
 */
@Data
public class UserPO {

    /**
     * 中文名称
     */
    private String chineseName;

    /**
     * 用户id
     */
    private String id;

    /*
     * 用户名
     */
    private String username;
}
