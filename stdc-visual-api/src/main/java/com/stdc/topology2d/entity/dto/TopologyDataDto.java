package com.stdc.topology2d.entity.dto;

import com.stdc.topology2d.entity.po.TopologyData;
import lombok.Data;


@Data
public class TopologyDataDto extends TopologyData {

    private String[] idList;

    private String sharedUrlPrefix;
}
