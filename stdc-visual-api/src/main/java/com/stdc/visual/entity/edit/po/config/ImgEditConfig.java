package com.stdc.visual.entity.edit.po.config;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/18--13:51
 * @describe: 工程化编辑 - 图片 - 复用配置 -配置类
 */
public enum ImgEditConfig implements VisualEdtConfigInterface{

    SIZE_LOCK("锁定图片尺寸","sizeLock,sizeLockObject"),

    OPACITY("透明度","opacity"),

    DYNAMIC_SWITCH("地址动态切换","dynamicSwitch"),

    DYNAMIC_EFFECTS("动态效果","animationType,angleOfInclination,rotateTime,rotateDirection,blinkTime,jumpTime,jumpDirection,jumpStart,jumpEnd"),

    ROTATE_EFFECTS("旋转设置","rotateAngle"),

    SELECT_BORDER_EFFECTS("选中边框设置","clickBorder,border,borderColor,borderShadow,borderShadowColor")

    ;



    private String name;

    private String config;

    ImgEditConfig(String name, String config) {
        this.name = name;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public String getConfig() {
        return config;
    }

}
