package com.stdc.visual.dynamic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.dynamic.base.datasource.po.DatasourceDriver;
import com.stdc.visual.dynamic.mapper.DatasourceDriverMapper;
import com.stdc.visual.dynamic.service.DatasourceDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/3/15--19:18
 * @describe:
 */
@Service
public class DatasourceDriverServiceImpl implements DatasourceDriverService {

    @Autowired
    private DatasourceDriverMapper driverMapper;

    @Value("${driver.path}")
    private String driverPath;

    @Override
    public List<DatasourceDriver> queryAll() {
        return driverMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public List<DatasourceDriver> queryListByType(String type) {
        LambdaQueryWrapper<DatasourceDriver> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasourceDriver::getType,type);
        List<DatasourceDriver> driverS = driverMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(driverS)){
            BaseException.throwException("数据源驱动不存在");
        }
        driverS.forEach(driver -> {
            String[] paths = driver.getJarPath().split(StringPool.SLASH);
            driver.setJarFileName(paths[paths.length -1]);
        });
        return driverS;
    }

    @Override
    public DatasourceDriver queryListById(String id) {
        LambdaQueryWrapper<DatasourceDriver> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasourceDriver::getId,id);
        DatasourceDriver driver = driverMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(driver)){
            BaseException.throwException("数据源驱动不存在");
        }
        String[] paths = driver.getJarPath().split(StringPool.SLASH);
        driver.setJarFileName(paths[paths.length -1]);
        return driver;
    }

    @Override
    public Boolean save(DatasourceDriver datasourceDriver) {
        LambdaQueryWrapper<DatasourceDriver> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasourceDriver::getName,datasourceDriver.getName());
        List<DatasourceDriver> drivers = driverMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(drivers)){
            //名称重复
            BaseException.throwException(ResultCode.get("name_cant_repeat_same_group"));
        }
        datasourceDriver.setId(StringUtil.randomUUID());
        return driverMapper.insert(datasourceDriver) > 0;
    }

    @Override
    public Boolean update(DatasourceDriver datasourceDriver) {
        return driverMapper.updateById(datasourceDriver) > 0;
    }

    @Override
    public Boolean del(String id) {
        return driverMapper.deleteById(id) > 0;
    }

    @Override
    public String uploadDriverJar(MultipartFile file) {
        if (!StringUtil.equalsIgnoreCase("jar",FileUtil.getFileExtension(file.getOriginalFilename()))){
            BaseException.throwException("文件格式错误,请上传jar文件");
        }
        String jarPath = driverPath + StringPool.SLASH + "custom" + StringPool.SLASH + StringUtil.randomUUID() +StringPool.SLASH + file.getOriginalFilename();
        try {
            FileUtil.uploadToPath(jarPath, file.getInputStream());
        } catch (IOException e) {
            LogUtil.error(e);
        }
        return jarPath;
    }

    @Override
    public Boolean delDriverJar(String jarPath) {
        File file = new File(jarPath);
        if (ObjectUtil.isEmpty(file) || !file.exists()) BaseException.throwException("文件不存在");
        boolean del = cn.hutool.core.io.FileUtil.del(file.getParentFile());
        //本地文件删除成功,即删除配置信息
        if (del){
            DatasourceDriver driver = new DatasourceDriver();
            driver.setJarPath("");
            driverMapper.update(driver,new LambdaQueryWrapper<DatasourceDriver>().eq(DatasourceDriver::getJarPath,jarPath));
        }
        return del;
    }
}
