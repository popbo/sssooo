package com.stdc.visual.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.SpringContextUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.UserRoleService;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.mapper.OssFileMapper;
import com.stdc.visual.mapper.VisualConfigMapper;
import com.stdc.visual.service.IVisualOssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.stdc.core.tool.utils.ObjectUtil.deepCopy;

/**
 * @author: wang_jie
 * @data: 2023/6/16--13:55
 * @describe: 定时更新oss_file表中的config字段(废弃于2024年12月31日14:41:05)
 */
@Slf4j
@Component
public class OssFileTask {

    @Autowired
    private VisualConfigMapper configMapper;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private CommonThreadPool commonThreadPool;

    /**
     * 凌晨五点触发更新
     */
//    @Scheduled(cron = "0 0 5 * * ?")
    public void initOssFileConfigIdOnNight() {
        OssFile updateOssFile = new OssFile();
        updateOssFile.setConfigId("0");
        updateOssFile.setVisualId("0");
        ossFileMapper.update(updateOssFile,Wrappers.emptyWrapper());
        initOssFileConfigId();
    }


//    @PostConstruct
    public void initOssFileConfigId(){
        commonThreadPool.addTask(()->{
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initOssFileConfig();
        });
    }
    /**
     *  更新oss_file表中的config字段
     */
    public void initOssFileConfig(){
        long timeMillis = System.currentTimeMillis();
        //获取所有大屏页面
        List<VisualConfig> configS = configMapper.selectList(Wrappers.emptyWrapper()).stream().filter(c->StringUtil.hasText(c.getComponent()) && StringUtil.hasText(c.getBackgroundId())).collect(Collectors.toList());
        //获取所有文件集合
        List<OssFile> allFilesS = ossFileMapper.queryAllWithSelect();
        //遍历大屏页面
        AtomicInteger configCount = new AtomicInteger();
        configS.forEach(config->{
            Long configId = config.getId();
            Long visualId = config.getVisualId();
            String backgroundId = config.getBackgroundId();
            String configStr = JSON.toJSONString(config);
            //找到当前大屏的文件集合
            List<OssFile> wantedOssFileS = allFilesS.stream().filter(ossFile -> StringUtil.equals(ossFile.getId(), backgroundId) || configStr.contains(ossFile.getLink())).collect(Collectors.toList());
            //设置configId
            deepCopy(wantedOssFileS).stream().forEach(ossFile ->{
                //更新configId
                ossFile.setLink(null);
                String oldConfigId =  ossFile.getConfigId();
                String oldVisualId = ossFile.getVisualId();
                String newConfigId = oldConfigId + StringPool.COMMA + configId;
                String newVisualId =  oldVisualId + StringPool.COMMA + visualId ;
                ossFile.setVisualId(newVisualId);
                ossFile.setConfigId(newConfigId);
                ossFileMapper.updateById(ossFile);
            });
            configCount.getAndIncrement();
            log.info("更新oss_file表中的config字段,初始化进度（初始化个数/大屏总个数）:"+configCount.get() + StringPool.SLASH + configS.size());
        });
        //大屏文件集合-visualId - configId 去重
        List<OssFile> ossFilesS = ossFileMapper.selectList(new LambdaQueryWrapper<OssFile>().select(OssFile::getId,OssFile::getLink,OssFile::getVisualId,OssFile::getConfigId));
        ossFilesS.forEach(ossFile -> {
            Set<String> visualSet = new HashSet();
            Set<String> configSet = new HashSet();
            if (StringUtil.hasText(ossFile.getVisualId())){
                for (String visualId : ossFile.getVisualId().split(",")) {
                    visualSet.add(visualId);
                }
            }
            if (StringUtil.hasText(ossFile.getConfigId())){
                for (String configId : ossFile.getConfigId().split(",")) {
                    configSet.add(configId);
                }
            }
            //更新configId
            ossFile.setLink(null);
            ossFile.setVisualId(String.join(StringPool.COMMA,visualSet  ));
            ossFile.setConfigId(String.join(StringPool.COMMA,configSet));
            ossFileMapper.updateById(ossFile);
        });
        log.info("初始化oss_file表中configId完成-->"+(System.currentTimeMillis() - timeMillis)/1000 + "S");
    }


    /**
     *  通过configId更新oss_file表中的visualId和configId字段
     */
    public void initOssFileByConfigId(Long configId){
        //获取大屏页面
        VisualConfig config = configMapper.selectById(configId);
        //获取所有oss文件
        //找到当前大屏的文件集合
        List<OssFile> ossFilesS = ossFileMapper.queryAllWithSelect();
        //遍历大屏页面
        Long visualId = config.getVisualId();
        String backgroundId = config.getBackgroundId();
        String configStr = JSON.toJSONString(config);
        CountDownLatch latch = new CountDownLatch(10);
        List<OssFile> wantedOssFileS = new CopyOnWriteArrayList();
        for (List<OssFile> ossFiles : splitList(ossFilesS, 10)) {
            commonThreadPool.addTask(()->{
                //找到当前大屏的文件集合
                List<OssFile> collects = ossFiles.stream().filter(ossFile -> StringUtil.equals(ossFile.getId(), backgroundId) || configStr.contains(ossFile.getLink())).collect(Collectors.toList());
                wantedOssFileS.addAll(collects);
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //设置configId
        deepCopy(wantedOssFileS).stream().forEach(ossFile ->{
            //更新configId
            ossFile.setLink(null);
            String oldConfigId =  ossFile.getConfigId();
            String oldVisualId = ossFile.getVisualId();
            String newConfigId = oldConfigId + StringPool.COMMA + configId;
            String newVisualId =  oldVisualId + StringPool.COMMA + visualId;
            ossFileMapper.updateByIdSelf(newVisualId,newConfigId,ossFile.getId());
        });
        log.info("更新oss_file表中的config字段,初始化进度 configId:"+configId);
    }

    /**
     *  通过visualId更新oss_file表中的visualId和configId字段
     */
    public void initOssFileByVisualId(Long visualId){
        //获取大屏页面
        LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(VisualConfig::getVisualId,visualId);
        List<VisualConfig> configs = configMapper.selectList(queryWrapper);
        configs.forEach(config -> {
            initOssFileByConfigId(config.getId());
        });
    }


    /**
     * 将一个list分成好多份
     * @param originalList
     * @param splitSize
     * @return
     */
    public static List<List<OssFile>> splitList(List<OssFile> originalList, int splitSize) {
        List<List<OssFile>> resultList = new ArrayList<>();
        int listSize = originalList.size();
        int batchSize = (listSize + splitSize - 1) / splitSize; // 计算每份的大小，向上取整
        for (int i = 0; i < listSize; i += batchSize) {
            int end = Math.min(i + batchSize, listSize);
            resultList.add(originalList.subList(i, end));
        }
        return resultList;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void initFrontCss(){
        commonThreadPool.addTask(()->initFrontCssByName("admin"));
    }

    public static void initFrontCssByName(String username){
        IVisualOssFileService iVisualOssFileService = SpringContextUtil.getBean(IVisualOssFileService.class);
        if (!iVisualOssFileService.writeFontCss(username)){
            log.info(username+"获取css文件失败");
        }
        log.info(username+"获取css文件成功");
    }


//    /**
//     * 2024年2月26日13:47:50初始化树结构苏州现场数据源，由表格改成,修改每一张大屏的数据源组件
//     */
//    @PostConstruct
//    public void initDataTreePostConstruct(){
//        initDataHttpTree("c146da49f797721709b8c924537661a0");
//        initDataWsTree("e2306cb813e84218651d48b27be82679");
//    }
//
//    private void initDataHttpTree(String categoryValue){
//        List<VisualConfig> configs = configMapper.queryConfigByCategoryValue(categoryValue);
//        for (VisualConfig config : configs) {
//            JSONArray arrayS = JSONArray.parseArray(config.getComponent());
//            for (Object oa : arrayS) {
//                JSONObject jsonObject = (JSONObject) oa;
//                JSONObject component = jsonObject.getJSONObject("component");
//                if (ObjectUtil.isNotEmpty(component) && component.getString("prop").equals("dataStorage")){
//                        jsonObject.put("dataFormatter","function(data,params,refs){\n" +
//                                "    let dataArr = data.data\n" +
//                                "    let realData = []    \n" +
//                                "    dataArr.forEach(item => {\n" +
//                                "        realData.push({            \n" +
//                                "        label:item.device_id + \"_\" + item.device_status,\n" +
//                                "        id:item.device_id,\n" +
//                                "        value:item.device_status + ''\n" +
//                                "        })    \n" +
//                                "    })   \n" +
//                                "    return [{           \n" +
//                                "        \"id\": \"1111\",            \n" +
//                                "        \"label\": \"root\",\n" +
//                                "        \"children\": realData\n" +
//                                "        }]\n" +
//                                "}");
//                        //获取绑定列表
//                        JSONArray comRelavanceList = jsonObject.getJSONArray("comRelavanceList");
//                        for (Object o : comRelavanceList) {
//                            JSONObject comRelavance = (JSONObject)o;
//                            //获取到组件id
//                            String componentId = comRelavance.getString("index");
//                            //找到对应的组件
//                            Optional<Object> index = arrayS.stream().filter(ob -> ((JSONObject) ob).getString("index").equals(componentId)).findFirst();
//                            if (index.isPresent()){
//                                JSONObject componentObj = (JSONObject)index.get();
//                                //找到对应的组件名称（设备id）
//                                String deviceId = componentObj.getString("name");
//                                //是否匹配设备id格式
//                                if (deviceId.contains("L06-S01")){
//                                    //设置组件列表的datavalue和treedata
//                                    comRelavance.put("dataValue",deviceId);
//                                    comRelavance.put("isTree",true);
//                                    Map<String,String> map = new HashMap<>();
//                                    map.put("id",deviceId);
//                                    map.put("label",deviceId);
//                                    comRelavance.put("treeData",map);
//                                }
//                            }else{
//                                log.info(config.getId() + " 的数据源组件绑定关系中,没有 " + componentId + "这个组件" );
//                            }
//                        }
//                        log.info(config.getId() + " 修改数据源组件完成----->");
//                    }else {
//                        log.info(config.getId() + " 没有数据源组件----->");
//                    }
//                }
//            config.setComponent(JSON.toJSONString(arrayS));
//            configMapper.updateById(config);
//            }
//        }
//    private void initDataWsTree(String categoryValue){
//        List<VisualConfig> configs = configMapper.queryConfigByCategoryValue(categoryValue);
//        for (VisualConfig config : configs) {
//            JSONArray arrayS = JSONArray.parseArray(config.getComponent());
//            for (Object oa : arrayS) {
//                JSONObject jsonObject = (JSONObject) oa;
//                JSONObject component = jsonObject.getJSONObject("component");
//                if (ObjectUtil.isNotEmpty(component) && component.getString("prop").equals("dataStorage")){
//                    jsonObject.put("dataFormatter","function(data,params,refs){\n" +
//                            "   let dataArr =JSON.parse(data)\n" +
//                            "   let testData = window.cache_ws_data;\n" +
//                            "   if(!testData){\n" +
//                            "     testData =  window.cache_ws_data = dataArr\n" +
//                            "   }\n" +
//                            "   \n" +
//                            "testData&&testData.forEach((item,index)=>{\n" +
//                            "    dataArr.forEach((el)=>{\n" +
//                            "        if(item.device_id === el.device_id){\n" +
//                            "            testData[index] = el\n" +
//                            "        }\n" +
//                            "    })\n" +
//                            "})\n" +
//                            "    let realData = [] \n" +
//                            "    dataArr.forEach(item => {\n" +
//                            "        realData.push({            \n" +
//                            "        label:item.device_id + \"_\" + item.device_status,\n" +
//                            "        id:item.device_id,\n" +
//                            "        value:item.device_status + ''\n" +
//                            "        })    \n" +
//                            "    })   \n" +
//                            "    return [{           \n" +
//                            "        \"id\": \"1111\",            \n" +
//                            "        \"label\": \"root\",\n" +
//                            "        \"children\": realData\n" +
//                            "        }]\n" +
//                            "}");
//                    //获取绑定列表
//                    JSONArray comRelavanceList = jsonObject.getJSONArray("comRelavanceList");
//                    for (Object o : comRelavanceList) {
//                        JSONObject comRelavance = (JSONObject)o;
//                        //获取到组件id
//                        String componentId = comRelavance.getString("index");
//                        //找到对应的组件
//                        Optional<Object> index = arrayS.stream().filter(ob -> ((JSONObject) ob).getString("index").equals(componentId)).findFirst();
//                        if (index.isPresent()){
//                            JSONObject componentObj = (JSONObject)index.get();
//                            //找到对应的组件名称（设备id）
//                            String deviceId = componentObj.getString("name");
//                            //是否匹配设备id格式
//                            if (deviceId.contains("L06-S01")){
//                                //设置组件列表的datavalue和treedata
//                                comRelavance.put("dataValue",deviceId);
//                                comRelavance.put("isTree",true);
//                                Map<String,String> map = new HashMap<>();
//                                map.put("id",deviceId);
//                                map.put("label",deviceId);
//                                comRelavance.put("treeData",map);
//                            }
//                        }else{
//                            log.info(config.getId() + " 的数据源组件绑定关系中,没有 " + componentId + "这个组件" );
//                        }
//                    }
//                    log.info(config.getId() + " 修改数据源组件完成----->");
//                }else {
//                    log.info(config.getId() + " 没有数据源组件----->");
//                }
//            }
//            config.setComponent(JSON.toJSONString(arrayS));
//            configMapper.updateById(config);
//        }
//    }
}
