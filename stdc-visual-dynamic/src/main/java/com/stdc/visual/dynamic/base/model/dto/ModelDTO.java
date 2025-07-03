package com.stdc.visual.dynamic.base.model.dto;

import com.stdc.visual.common.base.ITreeBase;
import com.stdc.visual.dynamic.base.model.po.ModelWithBLOBs;
import lombok.Data;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/19--15:59
 * @describe:
 */
@Data
public class ModelDTO extends ModelWithBLOBs implements ITreeBase<ModelDTO> {

    private String privileges;

    private List<ModelDTO> children;
    private long allLeafs = 0l;
}
