package com.stdc.visual.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.log.utils.LogUtil;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.auth.mapper.VisualReleaseMapper;
import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.entity.edit.dto.*;
import com.stdc.visual.entity.edit.po.ComponentEditType;
import com.stdc.visual.entity.edit.util.VisualEditUtil;
import com.stdc.visual.entity.edit.vo.TargetComponentVO;
import com.stdc.visual.entity.edit.vo.TemplateComponentVO;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.service.IVisualConfigService;
import com.stdc.visual.service.IVisualEditService;
import com.stdc.visual.service.IVisualReleaseService;
import com.stdc.visual.service.IVisualService;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author: wang_jie
 * @data: 2023/5/17--16:22
 * @describe:
 */
@Service
public class IVisualEditServiceImpl implements IVisualEditService {

    @Autowired
    private IVisualConfigService configService;

    /**
     * 需要复用渲染的组件集合
     */
    private static final List<String> HAVE_RENDERING = Arrays.asList("img","text","table");

    @Override
    public Map queryReplace(VisualEditDTO visualEditDTO) {
        AtomicInteger replaceCount = new AtomicInteger();
        AtomicInteger componentCount = new AtomicInteger();
        //设置 查询条件 和 查询字段
        LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId, VisualConfig::getDetail, VisualConfig::getComponent).eq(VisualConfig::getId, visualEditDTO.getConfigId());
        //查询大屏页面配置
        VisualConfig config = configService.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(config)){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //转为字符串
        JSONArray array = JSONArray.parseArray(config.getComponent());
        String configStr = JSON.toJSONString(array);
        String countStr = configStr;
        //全局替换 - 并且计算替换的个数
        for (ReplaceAllEditDTO replaceAllEdit : visualEditDTO.getReplaceAllEditS()) {
            int count = 0,len = configStr.length();
            while(countStr.indexOf(replaceAllEdit.getFindText()) != -1) {
                countStr = countStr.substring(countStr.indexOf(replaceAllEdit.getFindText()) + 1,countStr.length());
                count++;
            }
            replaceCount.getAndAdd(count);
        }
        //组件修改
        //递归获取所有组件列表
        JSONArray components = getAllComponentS(JSONArray.parseArray(configStr));
        //遍历需要修改的组件列表
        for (ComponentEditDTO componentEditDTO : visualEditDTO.getComponentEditDTOS()) {
            //模板组件
            boolean isExit = components.stream().filter(c -> StringUtil.equals(componentEditDTO.getTemplateComponentId(), ((JSONObject) c).getString("index"))).findFirst().isPresent();
            if (!isExit){
                continue;
            }
            JSONObject templateComponent = deepCopy((JSONObject)components.stream().filter(c -> StringUtil.equals(componentEditDTO.getTemplateComponentId(), ((JSONObject) c).getString("index"))).findFirst().get());
            //模板组件列表
            List<Object> targetComponentS = components.stream().filter(c -> componentEditDTO.getTargetComponentIdS().contains(((JSONObject) c).getString("index"))).collect(Collectors.toList());
            //遍历目标组件
            for (Object o : targetComponentS) {
                // 获取目标组件的下标
                int index = components.indexOf(o);
                //删除旧组件
                components.remove(index);
                //转为JSONObject对象 目标组件深拷贝
                JSONObject targetComponent = deepCopy((JSONObject) o);
                //模板组件深拷贝
                JSONObject template = deepCopy(templateComponent);
                //遍历操作类型  修改对应的属性
                componentEditDTO.getEditTypeS().forEach(editType -> {
                    //判断操作类型
                    switch (editType){
                        // 复用数据
                        case ComponentEditType.DATA:
                            componentCount.getAndIncrement();
                            break;
                        // 复用配置
                        case ComponentEditType.CONFIG:
                            //需要修改的 option属性 里面的配置
                            List<VisualSelect> editConfigS = componentEditDTO.getConfigS();
                            //遍历配置列表
                            editConfigS.forEach(editConfig->{
                                //属性集合
                                List<String> configS = Arrays.asList(editConfig.getValue().split(","));
                                //遍历属性列表 将 目标组件 的对应属性都修改为  模板组件 的对应属性
                                configS.forEach(c-> componentCount.getAndIncrement());
                            });
                            break;
                        // 复用条件
                        case ComponentEditType.CONDITION:
                            //获取模板组件的option属性
                            JSONObject templateSqlData = deepCopy((JSONObject)template.get("sqlData"));
                            //获取目标组件的option属性
                            JSONObject targetSqlData = deepCopy((JSONObject)targetComponent.get("sqlData"));
                            //替换条件数组
                            targetSqlData.put("whereCustomFilterS",deepCopy(templateSqlData.get("whereCustomFilterS")));
                            //记录日志
                            if (CollectionUtils.isNotEmpty(((JSONArray) templateSqlData.get("whereCustomFilterS")))){
                                componentCount.getAndAdd(((JSONArray) templateSqlData.get("whereCustomFilterS")).size());
                            }
                            break;
                        // 复用事件
                        case ComponentEditType.EVENT:
                            //获取模板组件 事件
                            JSONArray templateEventList = (JSONArray)template.get("eventList");
                            //记录日志
                            if (CollectionUtils.isNotEmpty(templateEventList)){
                                componentCount.getAndAdd(templateEventList.size());
                            }
                            break;
                        // 复用渲染
                        case ComponentEditType.RENDERING:
                            //获取模板组件 渲染 渲染
                            JSONArray drawConditionList = deepCopy((JSONArray) template.get("drawConditionList"));
                            //获取目标组件的option属性
                            JSONObject teOption = deepCopy((JSONObject)template.get("option"));
                            //获取模板组件的option属性
                            JSONObject taOption = deepCopy((JSONObject)targetComponent.get("option"));
                            taOption.put("dynamicSwitch",deepCopy(teOption.get("dynamicSwitch")));
                            //替换目标组件 渲染 事件
                            targetComponent.put("drawConditionList",drawConditionList);
                            //记录日志
                            if (CollectionUtils.isNotEmpty(drawConditionList)){
                                componentCount.getAndAdd(drawConditionList.size());
                            }
                            break;
                        default:
                            break;
                    }
                });
            }
        }
        Map<String,Integer> ret = new HashMap<>();
        ret.put("replaceCount",replaceCount.get());
        ret.put("componentCount",componentCount.get());
        return ret;
    }

    @Override
    public Integer visualEdit(VisualEditDTO visualEditDTO) {
        //设置 查询条件 和 查询字段
        LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId, VisualConfig::getDetail, VisualConfig::getComponent).eq(VisualConfig::getId, visualEditDTO.getConfigId());
        //查询大屏页面配置
        VisualConfig config = configService.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(config)){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        JSONArray array = JSONArray.parseArray(config.getComponent());
        //转为字符串
        String configStr = JSON.toJSONString(array);
        //全局替换 - 并且计算替换的个数
        for (ReplaceAllEditDTO replaceAllEdit : visualEditDTO.getReplaceAllEditS()) {
            configStr = configStr.replaceAll(replaceAllEdit.getFindText(),replaceAllEdit.getReplaceText());
        }
        //组件修改
        //递归获取所有组件列表
        JSONArray components = getAllComponentS(JSONArray.parseArray(configStr));
        //遍历需要修改的组件列表
        for (ComponentEditDTO componentEditDTO : visualEditDTO.getComponentEditDTOS()) {
            //模板组件
            boolean isExit = components.stream().filter(c -> StringUtil.equals(componentEditDTO.getTemplateComponentId(), ((JSONObject) c).getString("index"))).findFirst().isPresent();
            if (!isExit){
                continue;
            }
            JSONObject templateComponent = deepCopy((JSONObject)components.stream().filter(c -> StringUtil.equals(componentEditDTO.getTemplateComponentId(), ((JSONObject) c).getString("index"))).findFirst().get());
            //模板组件列表
            List<Object> targetComponentS = components.stream().filter(c -> componentEditDTO.getTargetComponentIdS().contains(((JSONObject) c).getString("index"))).collect(Collectors.toList());
            //遍历目标组件
            for (Object o : targetComponentS) {
                // 获取目标组件的下标
                int index = components.indexOf(o);
                //删除旧组件
                components.remove(index);
                //转为JSONObject对象 目标组件深拷贝
                JSONObject targetComponent = deepCopy((JSONObject) o);
                //模板组件深拷贝
                JSONObject template = deepCopy(templateComponent);
                //遍历操作类型  修改对应的属性
                componentEditDTO.getEditTypeS().forEach(editType -> {
                    //判断操作类型
                    switch (editType){
                        // 复用数据
                        case ComponentEditType.DATA:
                            //获取模板组件 数据类型
                            int dataType = template.getInteger("dataType");
                            //替换目标组件 数据类型
                            targetComponent.put("dataType",dataType);
                            //修改 静态数据 接口数据 数据库数据 实时接口数据
                            switch (dataType){
                                case 0:
                                    //静态数据
                                    targetComponent.put("data",deepCopy(template.get("data")));
                                    break;
                                case 1:
                                    //接口地址
                                    targetComponent.put("url",template.get("url"));
                                    //请求方式
                                    targetComponent.put("dataMethod",template.get("dataMethod"));
                                    //请求头
                                    targetComponent.put("dataHeader",template.get("dataHeader"));
                                    //请求参数
                                    targetComponent.put("dataQuery",template.get("dataQuery"));
                                    //认证方式
                                    targetComponent.put("apiAuthenticationMethod",template.get("apiAuthenticationMethod"));
                                    //认证参数
                                    targetComponent.put("apiAuthenticationForm",deepCopy(template.get("apiAuthenticationForm")));
                                    //数据处理
                                    targetComponent.put("dataFormatter",template.get("dataFormatter"));
                                    break;
                                case 2:
                                    //数据库数据
                                    targetComponent.put("sqlData",deepCopy(template.get("sqlData")));
                                    break;
                                case 3:
                                    //websocket数据
                                    targetComponent.put("webSocketUrl",template.get("webSocketUrl"));
                                    break;
                                default:
                                    break;
                            }
                            break;
                        // 复用配置
                        case ComponentEditType.CONFIG:
                            //获取模板组件的option属性
                            JSONObject templateOption = deepCopy((JSONObject)template.get("option"));
                            //获取目标组件的option属性
                            JSONObject targetOption = deepCopy((JSONObject)targetComponent.get("option"));
                            //需要修改的 option属性 里面的配置
                            List<VisualSelect> editConfigS = componentEditDTO.getConfigS();
                            //遍历配置列表
                            editConfigS.forEach(editConfig->{
                                //属性集合
                                List<String> configS = Arrays.asList(editConfig.getValue().split(","));
                                //遍历属性列表 将 目标组件 的对应属性都修改为  模板组件 的对应属性
                                configS.forEach(c-> targetOption.put(c,deepCopy(templateOption.get(c))));
                            });
                            //重新赋值引用对象
                            targetComponent.put("option",deepCopy(targetOption));
                            break;
                        // 复用条件
                        case ComponentEditType.CONDITION:
                            //获取模板组件的option属性
                            JSONObject templateSqlData = deepCopy((JSONObject)template.get("sqlData"));
                            //获取目标组件的option属性
                            JSONObject targetSqlData = deepCopy((JSONObject)targetComponent.get("sqlData"));
                            //替换条件数组
                            targetSqlData.put("whereCustomFilterS",deepCopy(templateSqlData.get("whereCustomFilterS")));
                            //重新赋值引用对象
                            targetComponent.put("sqlData",deepCopy(targetSqlData));
                            break;
                        // 复用事件
                        case ComponentEditType.EVENT:
                            //获取模板组件 事件
                            JSONArray templateEventList = (JSONArray)template.get("eventList");
                            //替换目标组件事件
                            targetComponent.put("eventList",templateEventList);
                            break;
                        // 复用渲染
                        case ComponentEditType.RENDERING:
                            //获取模板组件 渲染 渲染
                            JSONArray drawConditionList = deepCopy((JSONArray) template.get("drawConditionList"));
                            //获取目标组件的option属性
                            JSONObject teOption = deepCopy((JSONObject)template.get("option"));
                            //获取模板组件的option属性
                            JSONObject taOption = deepCopy((JSONObject)targetComponent.get("option"));
                            taOption.put("dynamicSwitch",deepCopy(teOption.get("dynamicSwitch")));
                            //替换目标组件 渲染 事件
                            targetComponent.put("drawConditionList",drawConditionList);
                            //重新赋值引用对象
                            targetComponent.put("option",deepCopy(taOption));
                            break;
                        default:
                            break;
                    }
                });
                //修改后的组件,重新加入组件列表
                components.add(index,targetComponent);
            }
        }
        //设置修改后的属性
        config.setComponent(JSON.toJSONString(components));
        config.setUpdateTime(System.currentTimeMillis());
        configService.updateById(config);
        return null;
    }

    @Override
    public void replacementtemporary() {
        List<VisualConfig> list = configService.list();
        int i = 0;
        for (VisualConfig c : list) {
            Long id = c.getId();
            ReplaceAllEditDTO replaceAllEditDTO = new ReplaceAllEditDTO();
            replaceAllEditDTO.setFindText("''");
            replaceAllEditDTO.setReplaceText("'");
            this.replacementtemporaryConfig(c,replaceAllEditDTO);
            i++;
            LogUtil.info("=======replaceIng  --> ( "+i+"/"+list.size()+" )  ==============================================================");
        }
        LogUtil.info("==================================初始化  Config   替换完成==============================================================");
        LogUtil.info("==================================初始化  Config   替换完成==============================================================");
        LogUtil.info("==================================初始化  Config   替换完成==============================================================");
        LogUtil.info("==================================初始化  Config   替换完成==============================================================");
        LogUtil.info("==================================初始化  Config   替换完成==============================================================");


        List<VisualRelease> visualReleases = releaseService.queryAll();
        int j = 0;
        for (VisualRelease r : visualReleases) {
            ReplaceAllEditDTO replaceAllEditDTO = new ReplaceAllEditDTO();
            replaceAllEditDTO.setFindText("''");
            replaceAllEditDTO.setReplaceText("'");
            this.replacementtemporaryRelease(r,replaceAllEditDTO);
            i++;
            LogUtil.info("=======replaceIng  --> ( "+j+"/"+visualReleases.size()+" )  ==============================================================");
        }
        LogUtil.info("==================================初始化  Release   替换完成==============================================================");
        LogUtil.info("==================================初始化  Release   替换完成==============================================================");
        LogUtil.info("==================================初始化  Release   替换完成==============================================================");
        LogUtil.info("==================================初始化  Release   替换完成==============================================================");
        LogUtil.info("==================================初始化  Release   替换完成==============================================================");
    }

    @Autowired
    private IVisualReleaseService releaseService;

    @Autowired
    private VisualReleaseMapper releaseMapper;

    private void replacementtemporaryConfig(VisualConfig config,ReplaceAllEditDTO replaceAllEditDTO ){
        JSONArray array = JSONArray.parseArray(config.getComponent());
        //转为字符串
        String configStr = JSON.toJSONString(array);
        String detail = config.getDetail();
        //全局替换 - 并且计算替换的个数
        configStr = configStr.replaceAll(replaceAllEditDTO.getFindText(),replaceAllEditDTO.getReplaceText());
        detail = detail.replaceAll(replaceAllEditDTO.getFindText(),replaceAllEditDTO.getReplaceText());
        //组件修改
        //递归获取所有组件列表
        JSONArray components = getAllComponentS(JSONArray.parseArray(configStr));
        //设置修改后的属性
        config.setComponent(JSON.toJSONString(components));
        config.setDetail(detail);
        configService.updateById(config);
        LogUtil.info("====  replace Config Success  -->  "+config.getId()+"    ==============================================================");
    }
    private void replacementtemporaryRelease(VisualRelease release,ReplaceAllEditDTO replaceAllEditDTO){
        JSONArray array = JSONArray.parseArray(release.getComponent());
        //转为字符串
        String configStr = JSON.toJSONString(array);
        String detail = release.getDetail();
        //全局替换 - 并且计算替换的个数
        configStr = configStr.replaceAll(replaceAllEditDTO.getFindText(),replaceAllEditDTO.getReplaceText());
        detail = detail.replaceAll(replaceAllEditDTO.getFindText(),replaceAllEditDTO.getReplaceText());
        //组件修改
        //递归获取所有组件列表
        JSONArray components = getAllComponentS(JSONArray.parseArray(configStr));
        //设置修改后的属性
        release.setComponent(JSON.toJSONString(components));
        release.setDetail(detail);
        releaseMapper.updateById(release);
        LogUtil.info("====  replace Release Success  -->  "+release.getId()+"    ==============================================================");
    }


    @Override
    public List<TemplateComponentVO> queryTemplate(Long configId) {
        List<TemplateComponentVO> retS = new ArrayList<>();
        //设置 查询条件 和 查询字段
        LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId, VisualConfig::getComponent).eq(VisualConfig::getId, configId);
        //查询大屏页面配置
        VisualConfig config = configService.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(config)){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //递归获取所有组件
        JSONArray components = getAllComponentS(JSONArray.parseArray(config.getComponent()));
        components.stream().map(c->(JSONObject)c).forEach(c->{
            retS.add(TemplateComponentVO.builder()
                    .id(c.getString("index"))
                    .name(c.getString("name")).build());
        });
        return retS;
    }

    @Override
    public List<VisualSelect> queryEditType(Long configId, String templateComponentId) {
        List<VisualSelect> ret = new ArrayList<>();
        //设置 查询条件 和 查询字段
        LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId, VisualConfig::getComponent).eq(VisualConfig::getId, configId);
        //查询大屏页面配置
        VisualConfig visualConfig = configService.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(visualConfig)){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //递归获取所有组件
        JSONArray components = getAllComponentS(JSONArray.parseArray(visualConfig.getComponent()));
        //获取模板组件
        boolean isExit = components.stream().map(c -> (JSONObject) c).filter(c -> StringUtil.equals(templateComponentId, c.getString("index"))).findFirst().isPresent();
        if (!isExit){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        JSONObject templateComponent = components.stream()
                .map(c -> (JSONObject) c)
                .filter(c -> StringUtil.equals(templateComponentId, c.getString("index")))
                .findFirst().get();
        //获取模板类型
        JSONObject component = (JSONObject)templateComponent.get("component");
        String prop = component.getString("prop");
        //复用渲染
        if (HAVE_RENDERING.contains(prop)){
            VisualSelect rendering = new VisualSelect();
            rendering.setLabel("复用渲染");
            rendering.setValue(ComponentEditType.RENDERING);
            ret.add(rendering);
        }
        VisualSelect data = new VisualSelect();
        data.setLabel("复用数据");
        data.setValue(ComponentEditType.DATA);
        ret.add(data);

        VisualSelect config = new VisualSelect();
        config.setLabel("复用配置");
        config.setValue(ComponentEditType.CONFIG);
        ret.add(config);

        VisualSelect event = new VisualSelect();
        event.setLabel("复用事件");
        event.setValue(ComponentEditType.EVENT);
        ret.add(event);

        VisualSelect condition = new VisualSelect();
        condition.setLabel("复用条件");
        condition.setValue(ComponentEditType.CONDITION);
        ret.add(condition);

        return ret;
    }

    @Override
    public List<VisualSelect> queryConfig(Long configId, String templateComponentId, String editType) {
        List<VisualSelect> ret = new ArrayList<>();
        //复用配置才有配置项
        if (editType.contains(ComponentEditType.CONFIG)){
            //设置 查询条件 和 查询字段
            LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId, VisualConfig::getComponent).eq(VisualConfig::getId, configId);
            //查询大屏页面配置
            VisualConfig visualConfig = configService.getOne(queryWrapper);
            if (ObjectUtil.isEmpty(visualConfig)){
                BaseException.throwException(ResultCode.get("query_data_error"));
            }
            JSONArray components = getAllComponentS(JSONArray.parseArray(visualConfig.getComponent()));
            //获取模板组件
            boolean isExit = components.stream().map(c -> (JSONObject) c).filter(c -> StringUtil.equals(templateComponentId, c.getString("index"))).findFirst().isPresent();
            if (!isExit){
                BaseException.throwException(ResultCode.get("query_data_error"));
            }
            JSONObject templateComponent = components.stream()
                    .map(c -> (JSONObject) c)
                    .filter(c -> StringUtil.equals(templateComponentId, c.getString("index")))
                    .findFirst().get();
            //获取模板类型
            JSONObject component = (JSONObject)templateComponent.get("component");
            String prop = component.getString("prop");
            //初始化 不同组件的配置项
            VisualEditUtil.initEditConfig(ret,prop);
        }
        return ret;
    }

    @Override
    public List<TargetComponentVO> queryTarget(Long configId, String templateComponentId) {
        List<TargetComponentVO> retS = new ArrayList<>();
        //设置 查询条件 和 查询字段
        LambdaQueryWrapper<VisualConfig> queryWrapper = new LambdaQueryWrapper<VisualConfig>().select(VisualConfig::getId, VisualConfig::getComponent).eq(VisualConfig::getId, configId);
        //查询大屏页面配置
        VisualConfig config = configService.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(config)){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        //递归获取所有组件
        JSONArray components = getAllComponentS(JSONArray.parseArray(config.getComponent()));
        //获取模板组件类型
        boolean isExit = components.stream()
                //转为 JSONObject 对象
                .map(c -> (JSONObject) c)
                //找到模板组件
                .filter(c -> StringUtil.equals(templateComponentId, c.getString("index")))
                //获取模板组件类型
                .map(c -> ((JSONObject) c.get("component")).getString("prop")).findFirst().isPresent();
        if (!isExit){
            BaseException.throwException(ResultCode.get("query_data_error"));
        }
        String prop =  components.stream()
                //转为 JSONObject 对象
                .map(c -> (JSONObject) c)
                //找到模板组件
                .filter(c -> StringUtil.equals(templateComponentId, c.getString("index")))
                //获取模板组件类型
                .map(c -> ((JSONObject) c.get("component")).getString("prop")).findFirst().get();
        //获取目标组件
        components.stream()
                //转为 JSONObject 对象
                .map(c->(JSONObject)c)
                //筛选和样板组件同类的组件 不包括样板组件
                .filter(c-> StringUtil.equals(prop,((JSONObject)c.get("component")).getString("prop")) &&
                                    !StringUtil.equals(templateComponentId,c.getString("index")))
                //添加集合
                .forEach(c-> retS.add(TargetComponentVO.builder()
                                .id(c.getString("index"))
                                .name(c.getString("name")).build()));
        return retS;
    }

    @Override
    public List getImgNameS(Long configId,String keyWord,String replaceWord) {
        List<String> wantTxtS = new ArrayList<>();
        VisualConfig visualConfig = configService.getOne(new LambdaQueryWrapper<VisualConfig>().eq(VisualConfig::getId, configId));
        if (StringUtil.hasText(visualConfig.getComponent())){
            JSONArray componentS = JSONArray.parseArray(visualConfig.getComponent());
            //筛选组件列表
            for (Object component : componentS) {
                JSONObject jsonObject =  (JSONObject)component;
                JSONObject c = (JSONObject)jsonObject.get("component");
                //筛选图片组件
                if (StringUtil.hasText(c.getString("prop")) && StringUtil.equals( "img",c.getString("prop"))){
                    String title = jsonObject.getString("name");
                    //筛选图片组件名称
                    if (StringUtil.hasText(title) && title.contains(keyWord)){
                        wantTxtS.add(title.replaceAll(replaceWord, StringPool.EMPTY));
                    }
                }
            }
        }
        return wantTxtS;
    }

    /**
     * 递归获取所有组件，包括文件夹里面的
     * @return
     */
    private JSONArray getAllComponentS(JSONArray components){
        JSONArray retS = new JSONArray();
        for (Object c : components) {
            //转为JSONObject对象
            JSONObject component = (JSONObject)c;
            //判断 component,children 属性   文件夹:不存在component,存在children
            Set<String> keyS = component.keySet();
            if (!keyS.contains("component") && keyS.contains("children")){
                JSONArray children = (JSONArray)component.get("children");
                JSONArray allChildrenS = getAllComponentS(children);
                retS.addAll(allChildrenS);
            }
            //非文件夹 直接添加
            else {
                retS.add(component);
            }
        }
        return retS;
    }

    /**深拷贝*/
    public static <T> T deepCopy(T src) {
        T target = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            target = (T) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

}
