package com.stdc.visual.dynamic.service;

import com.stdc.visual.dynamic.base.model.dto.ModelDTO;
import com.stdc.visual.dynamic.base.model.request.ModelRequest;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--16:12
 * @describe: 树形模型
 */
public interface ModelService {


    List<ModelDTO> queryModel(ModelRequest request);
}
