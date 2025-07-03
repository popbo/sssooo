package com.stdc.visual.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.entity.po.VisualCategory;
import com.stdc.visual.mapper.VisualCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wang_jie
 * @data: 2024/8/2--9:54
 * @describe: 初始化 stdc_visual_category 表
 */
@Slf4j
@Component
public class InitDbColumnUtil {


    @Value("${spring.datasource.url}")
    private String url;

    @Autowired
    private VisualCategoryMapper visualCategoryMapper;

    @PostConstruct
    public void init() {
        initCategorySource();
        initCategoryParentId();
        initCategoryCreateTime();
        initSysUserExtend();
        initOssFileSource();
    }

    /**
     * 初始化 stdc_visual_category 表 category_source 字段
     */
    private void initCategorySource(){
        String databaseName = extractDatabaseName(url);
        if (StringUtil.isBlank(databaseName)) {
            throw new RuntimeException("初始化数据库脚本失败...................................");
        }
        boolean columnExists = visualCategoryMapper.isColumnExists(databaseName);
        log.info("判断数据库 {} ,的 stdc_visual_category 表中是否含有 category_source 字段 : {} ", databaseName,columnExists);
        if (!columnExists) {
            log.info("stdc_visual_category 表中 新增 category_source 字段 , 并赋值为 0");
            visualCategoryMapper.addColumnIfNotExists();
        }
        columnExists = visualCategoryMapper.isColumnExists(databaseName);
        log.info("再次 判断数据库 {} ,的 stdc_visual_category 表中是否含有 category_source 字段 : {} ", databaseName,columnExists);
        //更新大屏模版为 99
        VisualCategory visualCategory = new VisualCategory();
        visualCategory.setCategorySource("99");
        LambdaQueryWrapper<VisualCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VisualCategory::getCategoryValue,"999999");
        visualCategoryMapper.update(visualCategory,queryWrapper);
    }

    /**
     * 初始化 stdc_visual_category 表 parent_id 字段
     */
    private void initCategoryParentId(){
        String databaseName = extractDatabaseName(url);
        if (StringUtil.isBlank(databaseName)) {
            throw new RuntimeException("初始化数据库脚本失败...................................");
        }
        boolean columnExists = visualCategoryMapper.isColumnExistsOfParentId(databaseName);
        log.info("判断数据库 {} ,的 stdc_visual_category 表中是否含有 parent_id 字段 : {} ", databaseName,columnExists);
        if (!columnExists) {
            log.info("stdc_visual_category 表中 新增 parent_id 字段 , 并赋值为 0");
            visualCategoryMapper.addColumnIfNotExistsOfParentId();
        }
        columnExists = visualCategoryMapper.isColumnExistsOfParentId(databaseName);
        log.info("再次 判断数据库 {} ,的 stdc_visual_category 表中是否含有 parent_id 字段 : {} ", databaseName,columnExists);
    }

    /**
     * 初始化 stdc_visual_category 表 create_time 字段
     */
    private void initCategoryCreateTime(){
        String databaseName = extractDatabaseName(url);
        if (StringUtil.isBlank(databaseName)) {
            throw new RuntimeException("初始化数据库脚本失败...................................");
        }
        boolean columnExists = visualCategoryMapper.isColumnExistsOfCreateTime(databaseName);
        log.info("判断数据库 {} ,的 stdc_visual_category 表中是否含有 create_time 字段 : {} ", databaseName,columnExists);
        if (!columnExists) {
            log.info("stdc_visual_category 表中 新增 create_time 字段 , 并赋值为 0");
            visualCategoryMapper.addColumnIfNotExistsOfCreateTime();
        }
        columnExists = visualCategoryMapper.isColumnExistsOfCreateTime(databaseName);
        log.info("再次 判断数据库 {} ,的 stdc_visual_category 表中是否含有 create_time 字段 : {} ", databaseName,columnExists);
    }


    /**
     * 初始化 stdc_visual_sys_user 表 extend 字段
     */
    private void initSysUserExtend(){
        String databaseName = extractDatabaseName(url);
        if (StringUtil.isBlank(databaseName)) {
            throw new RuntimeException("初始化数据库脚本失败...................................");
        }
        boolean columnExists = visualCategoryMapper.isColumnExistsOfExtend(databaseName);
        log.info("判断数据库 {} ,的 stdc_visual_sys_user 表中是否含有 extend 字段 : {} ", databaseName,columnExists);
        if (!columnExists) {
            log.info("stdc_visual_sys_user 表中 新增 extend 字段 , 并赋值为:0");
            visualCategoryMapper.addColumnIfNotExistsOfExtend();
        }
        columnExists = visualCategoryMapper.isColumnExistsOfExtend(databaseName);
        log.info("再次 判断数据库 {} ,的 stdc_visual_sys_user 表中是否含有 extend 字段 : {} ", databaseName,columnExists);
    }


    /**
     * 初始化 stdc_visual_oss_file 表 source 字段
     */
    private void initOssFileSource(){
        String databaseName = extractDatabaseName(url);
        if (StringUtil.isBlank(databaseName)) {
            throw new RuntimeException("初始化数据库脚本失败...................................");
        }
        boolean columnExists = visualCategoryMapper.isColumnExistsOfOssFileSource(databaseName);
        log.info("判断数据库 {} ,的 stdc_visual_oss_file 表中是否含有 source 字段 : {} ", databaseName,columnExists);
        if (!columnExists) {
            log.info("stdc_visual_oss_file 表中 新增 source 字段 , 并赋值为:VISUAL");
            visualCategoryMapper.addColumnIfNotExistsOfOfOssFileSource();
        }
        columnExists = visualCategoryMapper.isColumnExistsOfOssFileSource(databaseName);
        log.info("再次 判断数据库 {} ,的 stdc_visual_oss_file 表中是否含有 source 字段 : {} ", databaseName,columnExists);
    }












    /**
     *
     * 从JDBC URL中提取数据库名称
     *
     * @param jdbcUrl JDBC URL字符串
     * @return 数据库名称，如果URL格式不正确则返回null
     */
    public static String extractDatabaseName(String jdbcUrl) {
        // 定义用于匹配数据库名称的正则表达式
        // 假设URL格式遵循 jdbc:mysql://hostname:port/dbname?params 的模式
        String regex = "jdbc:mysql://.*?/([^?]+)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建matcher对象
        Matcher matcher = pattern.matcher(jdbcUrl);

        // 查找匹配项
        if (matcher.find()) {
            // 返回第一个捕获组的内容，即数据库名称
            return matcher.group(1);
        }

        // 如果没有找到匹配项，则返回null
        return null;
    }







}
