package com.stdc.topology2d.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.topology2d.entity.po.TopologyData;
import org.apache.ibatis.annotations.Param;

public interface TopologyDataMapper extends BaseMapper<TopologyData> {

    void saveData(@Param("topo") TopologyData topologyData);

}
