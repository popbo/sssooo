package com.stdc.visual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.entity.po.Visual;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 可视化表 Mapper 接口
 *
 * @author wangjie
 */
public interface VisualMapper extends BaseMapper<Visual> {

    /**
     * 更新大屏缩略图
     * @param id 大屏主键
     * @return 是否成功
     */
    boolean updateBackgroundUrl(@Param("id") Long id,@Param("backGroundId") String backGroundId);

    /**
     * 通过某一个大屏id获取和这个大屏相同分类的所有的大屏id
     * @param id
     * @return
     */
    List<Long> getSameCateGory(@Param("id") Long id);

}
