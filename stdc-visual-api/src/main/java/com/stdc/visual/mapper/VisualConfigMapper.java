package com.stdc.visual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.entity.dto.VisualVersionDTO;
import com.stdc.visual.entity.dto.VisualVersionVO;
import com.stdc.visual.entity.po.VisualConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 可视化配置表 Mapper 接口
 *
 * @author wangjie
 */
public interface VisualConfigMapper extends BaseMapper<VisualConfig> {

    List<VisualVersionVO> queryVisualVersion(@Param("visualId") Long visualId);

    boolean updateNewVersion(VisualVersionDTO visualVersionDTO);

    /**
     * 根据菜单分类查询大屏页面
     * @param categoryValue
     * @return
     */
    @Select("SELECT * FROM `stdc_visual_config` WHERE visual_id IN (SELECT id FROM stdc_visual_visual WHERE category = #{categoryValue})")
    List<VisualConfig> queryConfigByCategoryValue(@Param("categoryValue")String categoryValue);


}
