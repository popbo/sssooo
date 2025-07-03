package com.stdc.visual.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/17--9:41
 * @describe: 数据源表
 */
public interface DatasourceMapper extends BaseMapper<Datasource> {

    int insertSelective(Datasource record);

    List<Datasource> selectAll();

}
