package com.stdc.visual.service;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.entity.edit.dto.ReplaceAllEditDTO;
import com.stdc.visual.entity.edit.dto.VisualEditDTO;
import com.stdc.visual.entity.edit.vo.TargetComponentVO;
import com.stdc.visual.entity.edit.vo.TemplateComponentVO;

import java.util.List;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2023/5/17--16:22
 * @describe: 工程化编辑业务实现类
 */
public interface IVisualEditService {

    /**
     * 查询替换信息数量
     * @param visualEditDTO
     * @return
     */
    Map queryReplace(VisualEditDTO visualEditDTO);

    /**
     * 工程化编辑
     * @param visualEditDTO
     * @return
     */
    Integer visualEdit(VisualEditDTO visualEditDTO);



    /**
     * 工程化临时
     * @return
     */
    void replacementtemporary();

    /**
     * 获取样板组件
     * @param configId
     * @return
     */
    List<TemplateComponentVO> queryTemplate(Long configId);

    /**
     * 获取操作类型
     * @param configId
     * @return
     */
    List<VisualSelect>  queryEditType(Long configId, String templateComponentId);

    /**
     * 获取操配置项
     * @param configId
     * @return
     */
    List<VisualSelect> queryConfig(Long configId,String templateComponentId,String editType);

    /**
     * 获取操配置项
     * @param configId
     * @return
     */
    List<TargetComponentVO> queryTarget(Long configId, String templateComponentId);

    /**
     * 通过大屏id,获取相应图片的txt文件
     */
    List getImgNameS(Long configId,String keyWord,String replaceWord);

}
