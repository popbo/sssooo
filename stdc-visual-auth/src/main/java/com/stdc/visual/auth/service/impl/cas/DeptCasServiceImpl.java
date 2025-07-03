package com.stdc.visual.auth.service.impl.cas;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.visual.auth.entity.dept.dto.DeptDTO;
import com.stdc.visual.auth.entity.dept.po.DeptPO;
import com.stdc.visual.auth.mapper.DeptMapper;
import com.stdc.visual.auth.service.DeptService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/8/25--10:22
 * @describe: 统一认证 组织 -实现类
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.CAS, matchIfMissing = true)
public class DeptCasServiceImpl extends ServiceImpl<DeptMapper, DeptPO> implements DeptService {
    @Override
    public Boolean save(DeptDTO dto) {
        return null;
    }

    @Override
    public Boolean deleteById(String id) {
        return null;
    }

    @Override
    public Boolean update(DeptDTO dto) {
        return null;
    }

    @Override
    public List<DeptDTO> query(DeptDTO request) {
        return null;
    }

    @Override
    public DeptPO queryDeptById(String id) {
        return null;
    }

    @Override
    public List<DeptDTO> allDepts() {
        return null;
    }
}
