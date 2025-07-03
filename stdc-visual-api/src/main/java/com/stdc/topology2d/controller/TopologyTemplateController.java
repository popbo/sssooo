package com.stdc.topology2d.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.constants.TopologyConstants;
import com.stdc.topology2d.entity.dto.TopologyTemplateDto;
import com.stdc.topology2d.entity.po.TopologyTemplate;
import com.stdc.topology2d.service.ITopologyTemplateService;
import com.stdc.visual.common.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.stdc.core.oss.utils.FileUtil.getFileExtension;

@RestController
@RequestMapping("/topology/template")
@ApiSupport(author = "fengqiang", order = 16)
@Api(tags = "组态:组态模板")
@Slf4j
public class TopologyTemplateController {

    @Autowired
    private ITopologyTemplateService topologyTemplateService;

    @ApiOperation("新增模板")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public R<List> add(@RequestBody Map data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        // 保存模板
        try {
            List map = topologyTemplateService.saveByData(data);
            return R.data(map);
        } catch (Exception e) {
            log.error("保存模板失败:{}", e);
            return R.fail("保存模板失败:" + e.getMessage());
        }
    }

    @ApiOperation("更新模板")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<List> update(@RequestBody Map<String, Object> data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        // 更新模板
        try {
            List map = (topologyTemplateService.saveByData(data));
            return R.data(map);
        } catch (Exception e) {
            log.error("更新模板失败:{}", e);
            return R.fail("更新模板失败:" + e.getMessage());
        }
    }

    @ApiOperation("更新使用模板的大屏")
    @RequestMapping(value = "/topology/update", method = RequestMethod.POST)
    public R<List> topologyUpdate(@RequestBody Map<String, Object> data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }
        try {
            List map = (topologyTemplateService.updateByData(data));//更新模板
            long currentTimeMillis = System.currentTimeMillis();
            topologyTemplateService.topologyUpdate(data);//更新使用模板的大屏
            log.info("-------->更新使用模板的大屏完成,耗时："+(System.currentTimeMillis() - currentTimeMillis)+" ms");
            return R.data(map);
        } catch (Exception e) {
            log.error("更新使用模板的大屏失败:{}", e);
            return R.fail("更新使用模板的大屏失败:" + e.getMessage());
        }
    }

    @ApiOperation("组态模板获取")
    @RequestMapping(value = "/get", method = {RequestMethod.POST})
    public R get(@RequestBody TopologyTemplateDto topologyTemplateDto) {
        //id为空
        if (topologyTemplateDto == null) {
            log.error("请求模板id为空");
            return R.fail("请求模板id为空");
        }

        QueryWrapper<TopologyTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_TEMPLATE_MODULETYPE_PROPERTY, topologyTemplateDto.getTemplateId());
        TopologyTemplate topologyTemplate = topologyTemplateService.getOne(queryWrapper);
        //TopologyTemplate topologyTemplate = topologyTemplateService.getById(topologyTemplateDto.getId());
        if (topologyTemplate == null) {
            return R.fail("获取信息失败");
        }
        return R.data(JsonUtil.parseObject(topologyTemplate.getData()));
    }

    @ApiOperation("获取模板列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R list(@RequestBody TopologyTemplate data) throws Exception {
        if (null == data) {
            return R.fail("数据为空");
        }

        try {
            Query query = new Query();
            query.setCurrent(data.getCurrent());
            query.setSize(data.getPageSize());
            QueryWrapper<TopologyTemplate> queryWrapper = Condition.getQueryWrapper(data);
            IPage<TopologyTemplate> pages = topologyTemplateService.page(Condition.getPage(query), queryWrapper);
            List<JSONObject> list = new ArrayList<>();
            pages.getRecords().stream().forEach(item -> {
                JSONObject jsonObject = JsonUtil.parseObject(item.getData());
                jsonObject.put(ModelConstants.TOPOLOGY_TEMPLATE_FOLDER_PROPERTY, item.getFolder());
                jsonObject.put(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_ID, item.getTemplateId());
                jsonObject.put(TopologyConstants.TOPOLOGY_TEMPLATE_DATA_NAME,item.getName());
                list.add(jsonObject);
            });

            return R.data(list);
        } catch (Exception e) {
            log.error("获取模板列表失败:{}", e);
            return R.fail("获取模板列表失败:" + e.getMessage());
        }
    }

    @ApiOperation("删除模板列表")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R<Boolean> delete(@RequestBody TopologyTemplateDto data) throws Exception {
        String[] idList = data.getTemplateIdList();
        if (null == idList) {
            return R.fail("数据为空");
        }
        try {
            //删除成功数
            Integer successCount = 0;
            for (String templateId : idList) {
                QueryWrapper<TopologyTemplate> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(ModelConstants.TOPOLOGY_TEMPLATE_MODULETYPE_PROPERTY, templateId);
                topologyTemplateService.remove(queryWrapper);
                successCount++;
            }
            return R.success("成功删除" + successCount + "条数据");
        } catch (Exception e) {
            log.error("删除模板失败:{}", e);
            return R.fail("删除模板失败:" + e.getMessage());
        }
    }

    @ApiOperation("模板导出")
    @PostMapping(value = "/export/{id}")
    public R export(@PathVariable String id) throws Exception {
        try {
            return R.data(topologyTemplateService.export(id));
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }

    @ApiOperation("模板导入")
    @PostMapping(value = "/import/{name}")
    public R templateImport(@ApiParam(value = "上传文件") @RequestParam MultipartFile file,
                          @ApiParam(value = "模板名称") @PathVariable("name") String name) throws Exception {
        if (!StringUtil.equalsIgnoreCase("zip", getFileExtension(file.getOriginalFilename()))) {
            return R.fail("文件类型错误,请上传zip格式文件");
        }
        if (name.contains(".zip")) {
            name = FileUtil.removeLastElement(FileUtil.getNameWithoutExtension(name), StringPool.UNDERSCORE);
        }
        topologyTemplateService.templateImport(file, null, name);
        return R.success("导入成功");
    }

    @ApiOperation("根据工程id导出模板")
    @PostMapping(value = "/export/project/{projectId}")
    public R exportByProjectId(@PathVariable String projectId) throws Exception {
        try {
            return R.data(topologyTemplateService.exportByProjectId(projectId));
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return R.fail("导出失败");
    }
    @ApiOperation("导入模板到指定工程")
    @PostMapping(value = "/import/project/{projectId}")
    public R importByProjectId(@ApiParam(value = "上传文件") @RequestParam MultipartFile file,
                          @ApiParam(value = "工程id") @PathVariable("projectId") String projectId) throws Exception {
        if (!StringUtil.equalsIgnoreCase("zip", getFileExtension(file.getOriginalFilename()))) {
            return R.fail("文件类型错误,请上传zip格式文件");
        }
        topologyTemplateService.templateImportByProjectId(file, projectId);
        return R.success("导入成功");
    }

}

