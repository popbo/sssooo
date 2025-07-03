package com.stdc.visual.dynamic.base.model.request;

import com.stdc.visual.dynamic.base.model.dto.ModelDTO;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--16:01
 * @describe:
 */
@Data
public class ModelRequest extends ModelDTO {

    private String userId;
    private String privileges;
    private Integer datasetMode;
    private boolean clearEmptyDir;
    private List<String> modelInnerTypeArray;

}

