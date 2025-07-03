package com.stdc.visual.controller.datasource;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.visual.auth.annotation.ApiDecryptAes;
import com.stdc.visual.dynamic.base.datasource.po.Datasource;
import com.stdc.visual.dynamic.base.datasource.po.DatasourceDriver;
import com.stdc.visual.dynamic.service.DatasourceDriverService;
import com.stdc.visual.dynamic.service.DatasourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2022/5/17--18:31
 * @describe: 数据源：数据源管理
 */
@Api(tags = "数据源：数据源管理")
@ApiSupport(author = "wangjie",order = 8)
@RequestMapping("datasource")
@RestController
public class DatasourceController {

    @Resource
    private DatasourceService datasourceService;

    @Autowired
    private DatasourceDriverService datasourceDriverService;

    @ApiDecryptAes
    @ApiOperation("新增数据源")
    @PostMapping("/add")
    public R addDatasource(@RequestBody Datasource datasource) throws Exception {
        return R.data(datasourceService.addDatasource(datasource));
    }

    @ApiOperation("验证数据源")
    @PostMapping("/validate")
    public R validate(@RequestBody Datasource datasource) throws Exception {
        return datasourceService.validate(datasource);
    }

    @ApiOperation("验证数据源")
    @GetMapping("/validate/{datasourceId}")
    public R validate(@PathVariable String datasourceId) {
        return datasourceService.validate(datasourceId);
    }


    @ApiOperation("查询数据源列表")
    @GetMapping("/list")
    public Object getDatasourceList() throws Exception {
        return R.data(datasourceService.getDatasourceList());
    }

//    @ApiOperation("查询当前用户数据源")
//    @GetMapping("/list/{type}")
//    public Object getDatasourceListByType(@PathVariable String type) throws Exception {
//        return ((List<Datasource>)getDatasourceList()).stream().filter(datasourceDTO -> datasourceDTO.getType().equalsIgnoreCase(type)).collect(Collectors.toList());
//    }


    @ApiOperation("删除数据源")
    @PostMapping("/delete/{datasourceID}")
    public Object deleteDatasource(@PathVariable(value = "datasourceID") String datasourceID) throws Exception {
        return datasourceService.deleteDatasource(datasourceID);
    }

    @ApiDecryptAes
    @ApiOperation("更新数据源")
    @PostMapping("/update")
    public void updateDatasource(@RequestBody Datasource Datasource) {
        datasourceService.updateDatasource(Datasource);
    }

    @ApiOperation("查询数据源下属所有表")
    @PostMapping("/getTables")
    public R getTables(@RequestBody Datasource datasource) throws Exception {
        return R.data(datasourceService.getTables(datasource));
    }

    @ApiOperation("获取Schema")
    @PostMapping("/getSchema")
    public R getSchema(@RequestBody Datasource datasource) throws Exception {
        return R.data(datasourceService.getSchema(datasource));
    }

    @ApiOperation("获取数据库驱动列表")
    @GetMapping("/drivers")
    public R driverQuery(){
        return R.data(datasourceDriverService.queryAll());
    }

    @ApiOperation("通过驱动类型获取数据库驱动列表")
    @GetMapping("/driver/query/bytype/{type}")
    public R driverQueryByType(@PathVariable("type")String type){
        return R.data(datasourceDriverService.queryListByType(type));
    }

    @ApiOperation("通过数据源驱动id获取数据库驱动详情")
    @GetMapping("/driver/query/byid/{id}")
    public R driverQueryById(@PathVariable("id") String id){
        return R.data(datasourceDriverService.queryListById(id));
    }

    @ApiOperation("保存数据库驱动")
    @PutMapping("/driver/save")
    public R driverSave(@RequestBody DatasourceDriver datasourceDriver){
        return R.data(datasourceDriverService.save(datasourceDriver));
    }

    @ApiOperation("编辑数据库驱动")
    @PostMapping("/driver/update")
    public R driverUpdate(@RequestBody DatasourceDriver datasourceDriver){
        return R.data(datasourceDriverService.update(datasourceDriver));
    }

    @ApiOperation("删除数据库驱动")
    @DeleteMapping("/driver/del")
    public R driverDel(@RequestBody DatasourceDriver datasourceDriver){
        return R.data(datasourceDriverService.del(datasourceDriver.getId()));
    }

    @ApiOperation("上传数据库驱动jar包")
    @PostMapping("/driver/upload")
    public R driverUpload(@ApiParam(value = "上传文件") @RequestParam MultipartFile file,
                          @ApiParam(value = "驱动id") String id){
        String jarPtah = datasourceDriverService.uploadDriverJar(file);
        Map ret = new HashMap<String,String>();
        ret.put("jarPtah",jarPtah);
        ret.put("id",id);
        return R.data(ret);
    }


    @ApiOperation("删除数据库驱动jar包")
    @PostMapping("/driver/del")
    public R driverDel(@ApiParam(value = "jar包地址")@RequestBody Map<String,String> body){
        String jarPath = body.get("jarPath");
        Boolean del = datasourceDriverService.delDriverJar(jarPath);
        return R.status(del);
    }

}