package com.stdc.visual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.entity.po.OssFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/5/27--11:32
 * @describe: 文件存储表
 */
public interface OssFileMapper extends BaseMapper<OssFile> {

    /**
     * 根据文件类型查询当前用户所属下的文件
     * @param roleIds 角色id
     * @param type
     * @return
     */
    List<OssFile> queryFileByType(@Param("roleIds") List<String> roleIds,@Param("source") String source,@Param("type") String type,@Param("current")Integer current,@Param("size") Integer size);

    /**
     * 通过大屏id查询，但是记录里面没有保存大屏id字段的文件
     * 先查询大屏的 component 字段，通过 link 进行比对 component，找到 ossFile
     * @return
     */
    List<OssFile> queryFileWithVisualId(@Param("visualId") Long visualId);

    /**
     * 【查询背景图】
     * 通过大屏id查询，但是记录里面没有保存大屏id字段的文件
     * 先查询大屏的 component 字段，通过 link 进行比对 component，找到 ossFile
     * @return
     */
    OssFile queryBackGroundWithVisualId(@Param("visualId") Long visualId);

    /**
     * 根据文件类型查询当前用户所属下的文件个数
     * @param roleIds 角色id
     * @param type
     * @return
     */
    Integer queryCountByType(@Param("roleIds") List<String> roleIds,@Param("source") String source,@Param("type") String type);


    /**
     * 查询封面图 排除了这个封面图属于模板大屏的情况
     * @return
     */
    OssFile selectBackOssFileWithOutTemplate(@Param("id") String id);

    /**
     * 通过configid查询大屏页面里面的文件id
     * @param configId
     * @return
     */
    OssFile queryBackGroundWithConfigId(@Param("configId") Long configId);

    @Select(" select id,link,visual_id,config_id from stdc_visual_oss_file")
    List<OssFile> queryAllWithSelect();

    @Update(" update stdc_visual_oss_file set visual_id=#{visualId}, config_id=#{configId}, is_deleted = 0  where id=#{id}")
    boolean updateByIdSelf(@Param("visualId") String visualId,@Param("configId") String configId,@Param("id") String id);

}
