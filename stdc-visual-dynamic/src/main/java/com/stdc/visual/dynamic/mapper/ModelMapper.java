package com.stdc.visual.dynamic.mapper;

import com.stdc.visual.dynamic.base.model.dto.ModelDTO;
import com.stdc.visual.dynamic.base.model.request.ModelRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--16:03
 * @describe:
 */
public interface ModelMapper {

    List<ModelDTO> queryModel (@Param("record") ModelRequest record);

}
