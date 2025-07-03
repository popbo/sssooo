package com.stdc.topology2d.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.oss.vo.OssFileVO;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.topology2d.entity.dto.TopologyFileDto;
import com.stdc.topology2d.entity.po.TopologyFile;
import com.stdc.topology2d.entity.vo.TopologyFileListVo;
import com.stdc.topology2d.service.Topology2dOssFileService;
import com.stdc.visual.controller.oss.past.MinioTemplate;
import com.stdc.visual.controller.oss.past.OssTemplate;
import com.stdc.visual.entity.po.OssFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: feng_qiang
 * @date: 2025/04/29--11:19
 * @describe:
 */

@RestController
@RequestMapping("/topology/file")
@ApiSupport(author = "fengqiang", order = 16)
@Api(tags = "组态:文件管理")
@Slf4j
public class TopologyFileController {

    @Autowired
    private Topology2dOssFileService topology2dOssFileService;

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private OssTemplate ossTemplate;


    /**
     * 上传文件
     *
     * @param directory 上传缩略图传值为thumbnail，自定义上传封面图传值为custom， svg图上传svg,其他的为other
     */
    @SneakyThrows
    @PostMapping("/upload")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "上传文件", notes = "传入文件")
    public R<TopologyFileDto> upload(@ApiParam(value = "上传文件", required = true)
                                     @RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "filename", required = false) String filename,
                                     @RequestParam(value = "tags", required = false) String tags,
                                     @RequestParam(value = "share", required = false) String share,
                                     @RequestParam(value = "directory", required = false) String directory,
                                     @RequestParam(value = "remarks", required = false) String remarks) {
        TopologyFileDto topologyFileDto = topology2dOssFileService.save(directory, file);
        return R.data(topologyFileDto);
    }

    /**
     * 删除文件
     * 文件类型: svg(svg图片),back(背景图片),component(组件文件),others(其他文件),thumbnail(大屏封面)"
     *
     * @param path 上传缩略图传值为thumbnail，自定义上传封面图传值为custom， svg图上传svg,其他的为other
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/{path}")
    public R del(@ApiParam(value = "文件名") @PathVariable("path") String path) {
        boolean update = topology2dOssFileService.del(path);
        if (update) {
            return R.data(ResultCode.SUCCESS);
        }
        return R.fail(ResultCode.get("del_data_error"));
    }

    /**
     * 获取文件列表
     */
    @ApiOperation("获取当前用户文件夹下图片")
    @PostMapping("/list")
    public R list(@ApiParam(value = "获取文件列表") @RequestBody TopologyFileListVo topologyFileListVo) {
        R<IPage<TopologyFile>> ossFile = topology2dOssFileService.fileList(topologyFileListVo);
        if (ObjectUtils.isNotEmpty(ossFile)) {
            return ossFile;
        }
        return R.fail(ResultCode.get("del_data_error"));
    }

}

