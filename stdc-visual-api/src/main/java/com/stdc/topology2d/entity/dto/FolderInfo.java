package com.stdc.topology2d.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.stdc.topology2d.constants.ModelConstants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class FolderInfo {

    private String id;

    private String type;

    private String name;

    private List<Map<String, Object>> list = new ArrayList<>();

}
