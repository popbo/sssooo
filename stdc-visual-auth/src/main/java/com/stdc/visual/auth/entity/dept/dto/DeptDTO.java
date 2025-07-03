package com.stdc.visual.auth.entity.dept.dto;

import com.stdc.visual.auth.entity.dept.po.DeptPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:wang_jie
 * @data:2022/4/24--20:11
 * @describe:组织实体类
 */
@Data
@Accessors(chain = true)
public class DeptDTO extends DeptPO implements Serializable {

	private List<DeptDTO> children = new ArrayList<>();

	private static final long serialVersionUID = 1L;

}
