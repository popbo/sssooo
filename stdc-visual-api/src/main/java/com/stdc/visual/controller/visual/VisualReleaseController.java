package com.stdc.visual.controller.visual;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.service.IVisualService;
import com.stdc.visual.service.IVisualReleaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wang_jie
 * @data: 2022/10/18--18:30
 * @describe: 大屏发布控制类
 */
@ApiSupport(author = "wangjie")
@Api(tags = "大屏:大屏发布")
@RequestMapping("/release")
@RestController
public class VisualReleaseController {

    @Autowired
    private IVisualReleaseService releaseService;

    @Autowired
    private IVisualService iVisualService;



    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "大屏发布/大屏重新发布", notes = "大屏发布/大屏重新发布")
    @PostMapping(value = "/save")
    public R releaseVisual(@ApiParam(value = "大屏信息") @RequestBody VisualRelease release){
        if (releaseService.releaseVisual(release)){
            return R.success("发布成功");
        }else {
            return R.fail("发布失败");
        }
    }

    @ApiOperationSupport(order = 2,includeParameters = {"id","isRelease"})
    @ApiOperation(value = "修改大屏发布状态", notes = "修改大屏发布状态")
    @PostMapping(value = "/status")
    public R updateStatus(@ApiParam(value = "大屏信息") @RequestBody VisualRelease release){
        release.setIsEnc(null);
        release.setIsCustom(null);
        if (releaseService.updateReleaseById(release)){
            return R.success("更新成功");
        }else {
            return R.fail("更新失败");
        }
    }

    @ApiOperationSupport(order = 3,includeParameters = {"id"})
    @ApiOperation(value = "删除大屏发布信息", notes = "删除大屏发布信息")
    @PostMapping("/del")
    public R delReleaseById(@ApiParam(value = "大屏信息") @RequestBody VisualRelease release){
        return R.data(releaseService.delReleaseById(release));
    }


    @GetMapping("/queryAll")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "查询所有大屏发布链接", notes = "查询所有大屏发布链接")
    public R queryAll(){
        return R.data(releaseService.queryAll());
    }

    @GetMapping("/queryById/{id}")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "通过主键id查询大屏信息", notes = "通过主键id查询大屏信息")
    public R queryOneById(@PathVariable("id") String id){
        VisualRelease release = releaseService.queryOneById(id);
        return R.data(release == null ? new VisualRelease() : release);
    }

    @GetMapping("/queryByVisual/{visualId}")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "通过大屏id查询大屏信息", notes = "通过主键id查询大屏信息")
    public R queryByVisual(@PathVariable("visualId") Long visualId){
        VisualRelease release = releaseService.queryOneByVisualId(visualId);
        return R.data(release == null ? new VisualRelease() : release);
    }

    @GetMapping("/detail")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "通过地址获取大屏信息", notes = "通过地址获取大屏信息")
    public R queryByVisual(@Param("path") String  path, @Param("source") Integer source){
        if (source == null) source=0;
        VisualRelease release = releaseService.queryOneByPath(path, source);
        //没有地址
        if (ObjectUtil.isEmpty(release)){
            return R.fail(ResultCode.get("this_release_path_is_not_exit"));
        }
        //大屏未发布
        if (release.getIsRelease() == 0){
            return R.fail(ResultCode.get("this_release_path_is_not_release"));
        }
        //获取大屏信息
        VisualDTO detail = iVisualService.detailVersion(release.getId(),release.getConfigId());
        detail.getVisual().setRelease(release);
        //使用发布详情大屏配置备份
        //detail.getConfig().setDetail(release.getDetail());
        //detail.getConfig().setComponent(release.getComponent());
        return R.data(detail);
    }

}
