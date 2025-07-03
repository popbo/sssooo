package com.stdc.visual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.entity.po.VisualCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 可视化分类表 Mapper 接口
 *
 * @author wangjie
 */
public interface VisualCategoryMapper extends BaseMapper<VisualCategory> {

    @Select("SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = #{databaseName} AND  table_name = 'stdc_visual_category' AND column_name = 'category_source') THEN 1 ELSE 0 END")
    boolean isColumnExists(@Param("databaseName") String databaseName);


    @Update("ALTER TABLE `stdc_visual_category` ADD COLUMN `category_source` VARCHAR(255)  DEFAULT '0'")
    void addColumnIfNotExists();


    @Select("SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = #{databaseName} AND  table_name = 'stdc_visual_category' AND column_name = 'parent_id') THEN 1 ELSE 0 END")
    boolean isColumnExistsOfParentId(@Param("databaseName") String databaseName);


    @Update("ALTER TABLE `stdc_visual_category` ADD COLUMN `parent_id` VARCHAR(255)  DEFAULT '0'")
    void addColumnIfNotExistsOfParentId();



    @Select("SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = #{databaseName} AND  table_name = 'stdc_visual_category' AND column_name = 'create_time') THEN 1 ELSE 0 END")
    boolean isColumnExistsOfCreateTime(@Param("databaseName") String databaseName);


    @Update("ALTER TABLE `stdc_visual_category` ADD COLUMN `create_time` bigint(64) DEFAULT 0 COMMENT '创建时间' ")
    void addColumnIfNotExistsOfCreateTime();



    @Select("SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = #{databaseName} AND  table_name = 'stdc_visual_sys_user' AND column_name = 'extend') THEN 1 ELSE 0 END")
    boolean isColumnExistsOfExtend(@Param("databaseName") String databaseName);

    @Update("ALTER TABLE `stdc_visual_sys_user` ADD COLUMN `extend` VARCHAR(500)  DEFAULT '0'")
    void addColumnIfNotExistsOfExtend();


    /**
     * ossfile表是否存在source属性
     * @param databaseName
     * @return
     */
    @Select("SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = #{databaseName} AND  table_name = 'stdc_visual_oss_file' AND column_name = 'source') THEN 1 ELSE 0 END")
    boolean isColumnExistsOfOssFileSource(@Param("databaseName") String databaseName);

    /**
     * ossfile表新增source属性,赋值为 VISUAL
     * @return
     */
    @Update("ALTER TABLE `stdc_visual_oss_file` ADD COLUMN `source` VARCHAR(500)  DEFAULT 'VISUAL'")
    void addColumnIfNotExistsOfOfOssFileSource();

    VisualCategory getByCategoryValue(@Param("categoryValue") String categoryValue);
}
