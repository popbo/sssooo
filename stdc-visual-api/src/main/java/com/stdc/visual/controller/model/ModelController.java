package com.stdc.visual.controller.model;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.dynamic.base.model.dto.ModelDTO;
import com.stdc.visual.dynamic.base.model.request.ModelRequest;
import com.stdc.visual.dynamic.service.ModelService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--16:14
 * @describe: 树模型
 */
@Api(tags = "树：模型")
@ApiSupport(author = "wangjie",order = 9)
@RestController
//@RequestMapping("model")
@RequestMapping("authModel")
public class ModelController {

    @Resource
    private ModelService modelService;

    @PostMapping("/queryAuthModel")
    public R queryModel(@RequestBody ModelRequest request){
        return R.data(modelService.queryModel(request));
    }

}
