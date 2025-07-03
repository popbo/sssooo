package com.stdc.visual.auth.service.impl.own;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.visual.auth.entity.dept.dto.DeptDTO;
import com.stdc.visual.auth.entity.dept.po.DeptPO;
import com.stdc.visual.auth.mapper.DeptMapper;
import com.stdc.visual.auth.service.DeptService;
import com.stdc.visual.common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2022/5/30--18:52
 * @describe: 组织操作实现类
 */
@Service
@ConditionalOnProperty(value = AuthProperties.PREFIX + ".source", havingValue = AuthType.OWN, matchIfMissing = true)
public class DeptServiceImpl extends ServiceImpl<DeptMapper, DeptPO> implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public Boolean save(DeptDTO dto) {
        long timeMillis = System.currentTimeMillis();
        dto.setCreateTime(timeMillis);
        dto.setUpdateTime(timeMillis);
        int save = deptMapper.save(dto);
        return save > 0 ? true : false;
    }

    @Override
    public Boolean deleteById(String id) {
        int del = deptMapper.deleteByPrimaryKey(id);
        return del > 0 ? true : false;
    }

    @Override
    public Boolean update(DeptDTO dto) {
        dto.setUpdateTime(System.currentTimeMillis());
        int update = deptMapper.update(dto);
        return update > 0 ? true : false;
    }

    @Override
    public List<DeptDTO> query(DeptDTO request) {
        List<DeptDTO> query = deptMapper.query(request);
        return query;
    }

    @Override
    public DeptPO queryDeptById(String id) {
        return deptMapper.queryById(id);
    }

    @Override
    public List<DeptDTO> allDepts() {
        List<DeptPO> deptPOS = deptMapper.queryAll();
        return handlerDept(deptPOS);
    }

    /**
     * 处理组织为树状结构
     * @param deptPOS
     */
    private List<DeptDTO> handlerDept(List<DeptPO> deptPOS){
        List<DeptDTO> deptDTOS = new ArrayList<>();
        deptPOS.forEach(deptPO -> deptDTOS.add(BeanUtils.copyBean(new DeptDTO(),deptPO)));
        deptDTOS.forEach(deptDTO -> deptDTOS.forEach(d->{ if ( deptDTO.getPid() == d.getDeptId()) d.getChildren().add(deptDTO);}));
        deptDTOS.forEach(d -> {if (d.getChildren().size() == 0) d.setChildren(null);});
        List<DeptDTO> roots = deptDTOS.stream().filter(deptDTO -> deptDTO.getPid() == 0).collect(Collectors.toList());
        return roots;
    }


}