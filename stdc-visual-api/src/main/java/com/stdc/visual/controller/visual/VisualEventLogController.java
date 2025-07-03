package com.stdc.visual.controller.visual;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.entity.po.EventLogPO;
import com.stdc.visual.service.EventLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wang_jie
 * @data: 2024/1/4--15:55
 * @describe:
 */
@RestController
@RequestMapping("/visual/event")
@ApiSupport(author = "wangjie",order = 15)
@Api(tags = "大屏:组件日志")
public class VisualEventLogController {

    @Autowired
    private EventLogService eventLogService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增组件日志详情")
    @PostMapping(value = "/add")
    public R addEventLog(@ApiParam(value = "组件日志详情") @RequestBody EventLogPO eventLogPO){
        return R.status(eventLogService.addLog(eventLogPO));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "删除组件日志详情")
    @DeleteMapping(value = "/del/{id}")
    public R delEventLog(@ApiParam(value = "组件日志详情") @PathVariable("id") Integer id){
        return R.status(eventLogService.delLog(id));
    }

//    @ApiOperationSupport(order = 3)
//    @ApiOperation(value = "更新组件日志详情")
//    @PostMapping(value = "/update")
    public R updateEventLog(@ApiParam(value = "组件日志详情") @RequestBody EventLogPO eventLogPO){
        return R.status(eventLogService.updateLogById(eventLogPO));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "获取组件日志详情(列表)")
    @PostMapping(value = "/logs")
    public R queryEventLogs(@ApiParam(value = "组件日志详情") @RequestBody EventLogPO eventLogPO){
        return R.data(eventLogService.queryEventLogs(eventLogPO));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "通过主键获取组件日志详情")
    @PostMapping(value = "/queryOne/{id}")
    public R queryOneById(@ApiParam(value = "组件日志id") @PathVariable("id") Integer id){
        return R.data(eventLogService.queryOneById(id));
    }

}
