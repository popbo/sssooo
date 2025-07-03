package com.stdc.topology2d.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.oss.prop.OssFileType;
import com.stdc.core.oss.utils.OssTemplate;
import com.stdc.core.oss.vo.OssFileVO;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.DateUtil;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology2d.entity.po.CmpData;
import com.stdc.topology2d.entity.po.CmpVersion;
import com.stdc.topology2d.mapper.CmpDataMapper;
import com.stdc.topology2d.service.ICmpService;
import com.stdc.visual.common.utils.CommonThreadPool;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.entity.po.OssFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;

import static com.stdc.core.oss.utils.FileUtil.*;
import static com.stdc.core.oss.utils.FileUtil.delFile;

@Slf4j
@Service
public class CmpServiceImpl extends ServiceImpl<CmpDataMapper, CmpData> implements ICmpService {

    @Value("${cmp.version}")
    private String VERSION_URL;

    @Value("${cmp.data}")
    private String DATA_URL;

    public static final Integer SUCCESS_CODE = 1;

    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private CommonThreadPool commonThreadPool;

    @Override
    public CmpVersion getVersion() {
        String result = HttpUtil.get(VERSION_URL);
        JSONObject resultObject = JsonUtil.parseObject(result);
        if (SUCCESS_CODE.equals(resultObject.getInteger("code"))) {
            CmpVersion cmpVersion = JsonUtil.toJavaObj(resultObject.getString("data"), CmpVersion.class);
            return cmpVersion;
        } else {
            BaseException.throwException("获取版本失败：" + resultObject.getString("msg"));
        }
        return null;
    }

    @Override
    @Cacheable(value = "cmp", key = "#root.methodName", cacheManager = "cacheManager")
    public JSONObject getDeviceType() {
        JSONObject deviceTypeList = new JSONObject();
        String httpResult = HttpUtil.get(DATA_URL);
        JSONObject httpResultObject = JsonUtil.parseObject(httpResult);
        if (SUCCESS_CODE.equals(httpResultObject.getInteger("code"))) {
            // 系统数据列表systemList
            JSONArray systemListArray = httpResultObject.getJSONObject("data").getJSONArray("systemList");

            //物模型deviceProfileList     ----    device-type 如DTF1
            JSONArray deviceProfileArray = httpResultObject.getJSONObject("data").getJSONArray("deviceProfileList");
            deviceProfileArray.stream().forEach(deviceProfile -> {
                JSONObject deviceProfileObject = (JSONObject) deviceProfile;
                JSONObject deviceType = new JSONObject();
                deviceType.put("name", deviceProfileObject.getString("name"));
                JSONArray labelsArray = deviceProfileObject.getJSONArray("labels");
                labelsArray.stream().forEach(label -> {
                    systemListArray.stream().forEach(system -> {
                        JSONObject systemObject = (JSONObject) system;
                        if (systemObject.getString("id").equals(label)) {
                            String abbreviation = systemObject.getString("abbreviation");
                            String name = systemObject.getString("name");
                            deviceType.put("type", abbreviation);
                            deviceType.put("typeName", name);
                        }
                    });
                });


                JSONArray properties = new JSONArray();
                JSONArray functions = new JSONArray();

                deviceProfileObject.getJSONArray("deviceResources").stream().forEach(deviceResource -> {
                    JSONObject deviceResourceObject = (JSONObject) deviceResource;
                    String type = deviceResourceObject.getString("type");

                    //AO AI convertRule是对象
                    if (type.equals("AO") || type.equals("AI")) {
                        try {
                            String convertRule = deviceResourceObject.getString("convertRule");
                            JSONObject convertRuleObject = JsonUtil.parseObject(convertRule);
                            System.out.println("convertRuleObject:" + convertRuleObject);
                            if (type.equals("AO")) { //AO 写入functions
                                JSONArray action = new JSONArray();
                                JSONObject actionObject = new JSONObject();
                                actionObject.put("id", type + "_" + deviceResourceObject.getString("code"));
                                actionObject.put("name", deviceResourceObject.getString("name"));
                                if (convertRuleObject.containsKey("unit")) {
                                    actionObject.put("unit", convertRuleObject.getString("unit"));
                                }
                                action.add(actionObject);

                                JSONObject actionsObject = new JSONObject();
                                actionsObject.put("action", action);
                                actionsObject.put("executeDes", "执行");
                                JSONArray actions = new JSONArray();
                                actions.add(actionsObject);
                                JSONObject functionsObejct = new JSONObject();
                                functionsObejct.put("actions", actions);
                                functionsObejct.put("id", deviceResourceObject.getString("code") + "_control");
                                functionsObejct.put("name", deviceResourceObject.getString("name"));
                                functionsObejct.put("type", type);
                                functions.add(functionsObejct);
                            } else { //AI 写入properties
                                if (convertRuleObject.containsKey("unit")) {
                                    JSONObject propertiesObejct = new JSONObject();
                                    propertiesObejct.put("id", type + "_" + deviceResourceObject.getString("code"));
                                    propertiesObejct.put("name", deviceResourceObject.getString("name"));
                                    propertiesObejct.put("idType", type);
                                    propertiesObejct.put("unit", convertRuleObject.getString("unit"));
                                    properties.add(propertiesObejct);
                                }
                            }
                        } catch (Exception e) {
                            log.error("deviceResources.convertRule解析失败：" + e);
                        }

                    } else { //DO DI EO EI convertRule是数组
                        try {
                            String convertRule = deviceResourceObject.getString("convertRule");
                            JSONArray convertRuleArray = JSONArray.parseArray(convertRule);
                            System.out.println("convertRuleArray:" + convertRuleArray);
                            JSONArray options = new JSONArray();
                            JSONArray action = new JSONArray();
                            for (int i = 0; i < convertRuleArray.size(); i++) {
                                JSONObject convertRuleObject = convertRuleArray.getJSONObject(i);
                                //convertRule.description   =invalid时无效
                                String description = convertRuleObject.getString("description");
                                if (!description.equals("invalid")) {
                                    //type  = "DO" || "EO"控制 写入functions
                                    if (type.equals("DO") || type.equals("EO")) {
                                        JSONObject actionObject = new JSONObject();
                                        actionObject.put("id", type + "_" + deviceResourceObject.getString("code"));
                                        actionObject.put("name", convertRuleObject.getString("description"));
                                        actionObject.put("Value", convertRuleObject.getString("value"));
                                        action.add(actionObject);
                                    } else { //写入properties
                                        JSONObject optionsObject = new JSONObject();
                                        optionsObject.put("value", convertRuleObject.getString("value"));
                                        optionsObject.put("des", convertRuleObject.getString("description"));
                                        options.add(optionsObject);
                                    }
                                }
                            }
                            //type  = "DO" || "EO" 控制 写入functions
                            if (type.equals("DO") || type.equals("EO")) {
                                if (action.size() > 0) {
                                    JSONArray actions = new JSONArray();
                                    JSONObject actionsObject = new JSONObject();
                                    actionsObject.put("action", action);
                                    actionsObject.put("executeDes", "执行");
                                    actions.add(actionsObject);
                                    JSONObject functionsObejct = new JSONObject();
                                    functionsObejct.put("actions", actions);
                                    functionsObejct.put("id", deviceResourceObject.getString("code") + "_control");
                                    functionsObejct.put("name", deviceResourceObject.getString("name"));
                                    functionsObejct.put("type", type);
                                    functions.add(functionsObejct);
                                }
                            } else { //写入properties
                                if (options.size() > 0) {
                                    JSONObject propertiesObejct = new JSONObject();
                                    propertiesObejct.put("id", type + "_" + deviceResourceObject.getString("code"));
                                    propertiesObejct.put("name", deviceResourceObject.getString("name"));
                                    propertiesObejct.put("idType", type);
                                    propertiesObejct.put("options", options);
                                    properties.add(propertiesObejct);
                                }
                            }
                        } catch (Exception e) {
                            log.error("deviceResources.convertRule解析失败：" + e);
                        }
                    }
                });
                deviceType.put("properties", properties);
                deviceType.put("functions", functions);
                //listings固定写死
                deviceType.put("listings", getListings());
                deviceTypeList.put(deviceProfileObject.getString("code"), deviceType);

            });
        }
        return deviceTypeList;
    }

    @Override
    @Cacheable(value = "cmp", key = "#root.methodName", cacheManager = "cacheManager")
    public JSONArray getDeviceCorresponding() {
        //device-corresponding文件
        JSONArray deviceCorresponding = new JSONArray();
        String httpResult = HttpUtil.get(DATA_URL);
        JSONObject httpResultObject = JsonUtil.parseObject(httpResult);
        if (SUCCESS_CODE.equals(httpResultObject.getInteger("code"))) {
            // 系统节点列表systemNodeList  车站
            JSONArray systemNodeListArray = httpResultObject.getJSONObject("data").getJSONArray("systemNodeList");
            // 系统数据列表systemList
            JSONArray systemListArray = httpResultObject.getJSONObject("data").getJSONArray("systemList");
            //
            JSONArray deviceProfileArray = httpResultObject.getJSONObject("data").getJSONArray("deviceProfileList");

            // 设备列表deviceList
            JSONArray deviceListArray = httpResultObject.getJSONObject("data").getJSONArray("deviceList");
            deviceListArray.stream().forEach(device -> {
                JSONObject deviceObject = (JSONObject) device;
                // 开始组装设备信息
                //device-corresponding文件属性
                JSONObject deviceCorrespondingObject = new JSONObject();
                StringBuilder deviceIdBuilder = new StringBuilder();
                deviceCorrespondingObject.put("area", deviceObject.getString("location"));
                //station 从systemNodeList.name取
                systemNodeListArray.stream().forEach(systemNode -> {
                    JSONObject systemNodeObject = (JSONObject) systemNode;
                    if (deviceObject.getString("systemNodeId").equals(systemNodeObject.getString("id"))) {
                        deviceCorrespondingObject.put("station", systemNodeObject.getString("name"));
                        //deviceId组装
                        deviceIdBuilder.append(systemNodeObject.getString("code")).append("_");
                    }
                });
                //systemName 从systemList.name取
                systemListArray.stream().forEach(system -> {
                    JSONObject systemObject = (JSONObject) system;
                    if (deviceObject.getString("childSystemId").equals(systemObject.getString("id"))) {
                        deviceCorrespondingObject.put("systemName", systemObject.getString("name"));
                        //deviceId组装
                        deviceIdBuilder.append(systemObject.getString("abbreviation")).append("_");
                    }
                });

                //deviceTypeId、deviceTypeName 从deviceProfileList取
                deviceProfileArray.stream().forEach(deviceProfile -> {
                    JSONObject deviceProfileObject = (JSONObject) deviceProfile;
                    if (deviceObject.getString("deviceProfileId").equals(deviceProfileObject.getString("id"))) {
                        deviceCorrespondingObject.put("deviceTypeId", deviceProfileObject.getString("code"));
                        deviceCorrespondingObject.put("deviceTypeName", deviceProfileObject.getString("name"));
                        //deviceId组装
                        deviceIdBuilder.append(deviceProfileObject.getString("code")).append("_");
                    }
                });

                //deviceId组装
                deviceIdBuilder.append(deviceObject.getString("code"));
                deviceCorrespondingObject.put("deviceId", deviceIdBuilder.toString());
                //device-corresponding文件属性添加到device-corresponding文件
                deviceCorresponding.add(deviceCorrespondingObject);
            });

        }
        return deviceCorresponding;
    }

    @Override
    public String exportDeviceType() throws Exception{
        String result=null;
        JSONObject deviceType = this.getDeviceType();

        //缓存地址
        String cachePath = "./static/export" + StringPool.SLASH + StringUtil.randomUUID();
        JsonUtil.writeJsonToFile("var deviceTypeList = " +deviceType.toJSONString(),cachePath + StringPool.SLASH + "device-type.js");

        FileInputStream inputStream = null;
        String fileName = null;
        try {
            fileName = "device-type" + DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + ".js";
            File file = new File(cachePath + StringPool.SLASH + "device-type.js");
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", fileName, inputStream);
            result = save.getLink();
        } catch (Exception e) {
            log.error("下载本地文件失败...");
            e.printStackTrace();
        } finally {
            if (inputStream!=null) inputStream.close();
            //删除本地缓存文件
            delFile(cachePath);
            String finalFileName = fileName;
            commonThreadPool.addTask(()->{
                try {
                    Thread.sleep(20000);
                    delFile(finalFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return result;
    }

    @Override
    public String exportDeviceCorresponding() throws Exception{
        String result=null;
        JSONArray deviceCorresponding = this.getDeviceCorresponding();

        //缓存地址
        String cachePath = "./static/export" + StringPool.SLASH + StringUtil.randomUUID();
        JsonUtil.writeJsonToFile("var deviceCorresponding = " +deviceCorresponding.toJSONString(),cachePath + StringPool.SLASH + "device-corresponding.js");

        FileInputStream inputStream = null;
        String fileName = null;
        try {
            fileName = "device-corresponding" + DateUtil.format(new Date(),"yyyyMMddHHmmssSSS") + ".js";
            File file = new File(cachePath + StringPool.SLASH + "device-corresponding.js");
            inputStream = new FileInputStream(file);
            OssFile save = saveOssFileWithFileName("custom", "others", fileName, inputStream);
            result = save.getLink();
        } catch (Exception e) {
            log.error("下载本地文件失败...");
            e.printStackTrace();
        } finally {
            if (inputStream!=null) inputStream.close();
            //删除本地缓存文件
            delFile(cachePath);
            String finalFileName = fileName;
            commonThreadPool.addTask(()->{
                try {
                    Thread.sleep(20000);
                    delFile(finalFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return result;
    }

    private JSONArray getListings() {
        String listingStr = "[{\"id\":\"DO_RTDB_STOPEQCJ\",\"name\":\"脱离扫描\",\"idType\":\"DO\",\"actions\":[{\"action\":[{\"id\":\"DO_RTDB_STOPEQCJ\",\"name\":\"取消脱离扫描\",\"Value\":0},{\"id\":\"DO_RTDB_STOPEQCJ\",\"name\":\"脱离扫描\",\"Value\":1}],\"executeDes\":\"执行\"}]},{\"id\":\"DO_RTDB_STOPEQKZ\",\"name\":\"控制抑制\",\"idType\":\"DO\",\"actions\":[{\"action\":[{\"id\":\"DO_RTDB_STOPEQKZ\",\"name\":\"取消控制抑制\",\"Value\":0},{\"id\":\"DO_RTDB_STOPEQKZ\",\"name\":\"控制抑制\",\"Value\":1}],\"executeDes\":\"执行\"}]}]";
        JSONArray listings = JSONArray.parseArray(listingStr);
        return listings;
    }

    public OssFile saveOssFileWithFileName(String source, String type, String fileName, InputStream inputStream) {
        OssFileVO ossFileVO = ossTemplate.putFile(StdcVisualConstant.OSS_TOPOLOGY_PREFIX_BUCKET, source + StringPool.SLASH +fileNameHandler(fileName, type), inputStream);
        if (ObjectUtil.isEmpty(ossFileVO)){
            BaseException.throwException(ResultCode.get("upload_file_error"));
        }
        OssFile ossFile = new OssFile();
        String id = StringUtil.randomUUID();
        long timeMillis = System.currentTimeMillis();
        String namePrefix = null;
        ossFile.setId(id)
                .setNamePrefix(StringUtil.hasText(namePrefix) ? namePrefix : getNameWithoutExtension(fileName))
                .setNameSuffix(getFileExtension(fileName))
                .setLink(ossFileVO.getLink())
                .setDoMain(ossFileVO.getDoMain())
                .setType(type)
                .setSystemType(source)
                .setIsDeleted(0)
                .setCreateTime(timeMillis)
                .setUpdateTime(timeMillis);
        return ossFile;
    }

    private String fileNameHandler(String fileName,String type){

        OssFileType fileType = OssFileType.get(type);
        //校验文件格式
        String fileNameSuffix = getFileExtension(fileName);
        if (OssFileType.isNotInValues(fileType,fileNameSuffix)){
            BaseException.throwException(ResultCode.get("file_format_error"));
        }
        return fileType.getKey() + StringPool.SLASH + fileName;
    }


}
