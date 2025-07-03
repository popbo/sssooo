package com.stdc.topology2d.entity.dto;

import com.stdc.topology2d.entity.po.TopologyTemplate;
import lombok.Data;


@Data
public class TopologyTemplateDto extends TopologyTemplate {

    private String[] templateIdList;
}
