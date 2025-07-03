package com.stdc.visual.controller.visual;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.entity.edit.dto.VisualEditDTO;
import com.stdc.visual.service.IVisualEditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/17--18:56
 * @describe:
 */
@RestController
@RequestMapping("/visual/project")
@ApiSupport(author = "wangjie",order = 14)
@Api(tags = "大屏:工程化编辑")
public class VisualEditController {

    @Autowired
    private IVisualEditService visualEditService;

    /**
     * 查询全局替换的参数替换次数
     */
    @PostMapping("/query/replace")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询全局替换的参数替换次数")
    public R queryReplace(@RequestBody VisualEditDTO visualEditDTO) {
        return R.data(visualEditService.queryReplace(visualEditDTO));
    }

    /**
     * 一键替换
     */
    @PostMapping("/replaceAll")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "一键替换")
    public R replacement(@RequestBody VisualEditDTO visualEditDTO) {
        return R.data(visualEditService.visualEdit(visualEditDTO));
    }


    /**
     * 一键替换
     */
    @GetMapping("/replaceAll/temporary")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "一键替换")
    public void replacementtemporary() {
        visualEditService.replacementtemporary();
    }


    /**
     * 通过configId获取样板组件
     */
    @GetMapping("/template/{configId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "通过configId获取样板组件")
    public R template(@PathVariable("configId") Long configId) {
        return R.data(visualEditService.queryTemplate(configId));
    }


    /**
     * 通过 configId,样板模板id 获取操作类型
     */
    @GetMapping("/editType")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "通过 configId,样板模板id 获取操作类型")
    public R editType(@ApiParam(value = "页面配置id") @RequestParam("configId") Long configId,
                    @ApiParam(value = "样板模板id") @RequestParam("templateComponentId") String templateComponentId) {
        return R.data(visualEditService.queryEditType(configId,templateComponentId));
    }

    /**
     * 通过 configId,样板模板id 获取配置项
     */
    @GetMapping("/config")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "通过 configId,样板模板id 获取配置项")
    public R config(@ApiParam(value = "页面配置id") @RequestParam("configId") Long configId,
                    @ApiParam(value = "样板模板id") @RequestParam("templateComponentId") String templateComponentId,
                    @ApiParam(value = "操作类型") @RequestParam("editType") String editType) {
        return R.data(visualEditService.queryConfig(configId,templateComponentId,editType));
    }


    /**
     * 通过 configId,样板模板id,操作类型 获取目标修改组件列表
     */
    @GetMapping("/target")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "通过 configId,样板模板id,操作类型 获取目标修改组件列表")
    public R target(@ApiParam(value = "页面配置id") @RequestParam("configId") Long configId,
                    @ApiParam(value = "样板模板id") @RequestParam("templateComponentId") String templateComponentId) {
        return R.data(visualEditService.queryTarget(configId,templateComponentId));


    }

    /**
     * 通过大屏页面id,获取相应图片的txt
     */
    @GetMapping("/get/img-names")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "通过大屏页面id,,获取相应图片的txt")
    public R getImgNameS(@ApiParam(value = "大屏页面配置id") @RequestParam("configId") Long configId,
                    @ApiParam(value = "模糊查询keyword,比如 L06-S01-") @RequestParam("keyWord") String keyWord,
                            @ApiParam(value = "替换word,比如 L06-S01-") @RequestParam("replaceWord") String replaceWord) {
        return R.data(visualEditService.getImgNameS(configId,keyWord,replaceWord));
    }









}
