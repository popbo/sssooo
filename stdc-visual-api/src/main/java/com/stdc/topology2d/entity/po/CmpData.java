package com.stdc.topology2d.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "CMP配置数据", description = "CMP配置数据")
public class CmpData {

    @ApiModelProperty(value = "发布ID", name = "发布ID")
    private String releaseId;

    @ApiModelProperty(value = "版本号", name = "版本号")
    private String version;

    @ApiModelProperty(value = "发布时间时间戳", name = "发布时间时间戳")
    private String time;

    @ApiModelProperty(value = "发布标题", name = "发布标题")
    private String title;

    @ApiModelProperty(value = "发布描述", name = "发布描述")
    private String description;

    @ApiModelProperty(value = "MD5校验值", name = "MD5校验值")
    private String md5;

    @ApiModelProperty(value = "物模型", name = "物模型")
    private List<deviceProfile> deviceProfileList;

    //物模型
    public class deviceProfile {
        @ApiModelProperty(value = "物模型ID", name = "物模型ID")
        private String id;
        @ApiModelProperty(value = "工程ID", name = "工程ID")
        private String projectId;
        @ApiModelProperty(value = "编号", name = "编号")
        private String number;
        @ApiModelProperty(value = "编码", name = "编码")
        private String code;
        @ApiModelProperty(value = "名称", name = "名称")
        private String name;
        @ApiModelProperty(value = "制造商", name = "制造商")
        private String manufacturer;
        @ApiModelProperty(value = "型号", name = "型号")
        private String model;
        @ApiModelProperty(value = "脱离扫描", name = "脱离扫描")
        private String divorceScan;
        @ApiModelProperty(value = "控制抑制", name = "控制抑制")
        private String controlSuppress;
        @ApiModelProperty(value = "连接状态", name = "连接状态")
        private String connectionStatus;
        @ApiModelProperty(value = "关联系统", name = "关联系统")
        private List<String> labels;
        @ApiModelProperty(value = "物模型属性", name = "物模型属性")
        private List<deviceResource> deviceResources;
        @ApiModelProperty(value = "设备命令", name = "设备命令")
        private List<deviceCommand> deviceCommands;

        public class deviceResource {
            @ApiModelProperty(value = "物模型属性ID", name = "物模型属性ID")
            private String id;
            @ApiModelProperty(value = "工程ID", name = "工程ID")
            private String projectId;
            @ApiModelProperty(value = "关联物模型ID", name = "关联物模型ID")
            private String deviceProfileId;
            @ApiModelProperty(value = "编号", name = "编号")
            private String number;
            @ApiModelProperty(value = "编码", name = "编码")
            private String code;
            @ApiModelProperty(value = "名称", name = "名称")
            private String name;
            @ApiModelProperty(value = "类型(AI/DI/EI/AO/DO/EO/TXT)", name = "类型(AI/DI/EI/AO/DO/EO/TXT)")
            private String type;
            @ApiModelProperty(value = "转换规则(JSON格式)", name = "转换规则(JSON格式)")
            private String convertRule;
            @ApiModelProperty(value = "历史配置(JSON格式)", name = "历史配置(JSON格式)")
            private String history;
        }

        public class deviceCommand {
            @ApiModelProperty(value = "对应属性ID", name = "对应属性ID")
            private String resId;
            @ApiModelProperty(value = "对应属性名称", name = "对应属性名称")
            private String resName;
            @ApiModelProperty(value = "对应属性编码", name = "对应属性编码")
            private String resCode;
            @ApiModelProperty(value = "对应属性类型", name = "对应属性类型")
            private String resType;
            @ApiModelProperty(value = "设备内可控条件判断", name = "设备内可控条件判断")
            private List<String> preCondition;
            @ApiModelProperty(value = "跨设备可控条件判断", name = "跨设备可控条件判断")
            private List<String> preConditionExt;
            @ApiModelProperty(value = "控制反馈条件", name = "控制反馈条件")
            private List<String> resultCheck;
            @ApiModelProperty(value = "超时监测", name = "超时监测")
            private String timeout;
        }
    }

}
