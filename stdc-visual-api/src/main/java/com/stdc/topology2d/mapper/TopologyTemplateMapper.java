package com.stdc.topology2d.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.topology2d.entity.po.TopologyData;
import com.stdc.topology2d.entity.po.TopologyTemplate;
import org.apache.ibatis.annotations.Param;

public interface TopologyTemplateMapper extends BaseMapper<TopologyTemplate> {

    void saveTemplate(@Param("template") TopologyTemplate topologyTemplate);

}
