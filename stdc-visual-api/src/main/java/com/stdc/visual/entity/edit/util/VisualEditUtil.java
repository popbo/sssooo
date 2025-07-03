package com.stdc.visual.entity.edit.util;

import com.stdc.visual.dynamic.base.dataset.dto.component.select.VisualSelect;
import com.stdc.visual.entity.edit.po.ComponentProp;
import com.stdc.visual.entity.edit.po.config.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2023/5/19--10:25
 * @describe: 工程化编辑工具类
 */
public class VisualEditUtil {

    /**
     * 初始化 不同组件的配置项
     * @param ret
     * @param prop
     */
    public static void initEditConfig(List<VisualSelect> ret,String prop){
        //不同的模板类型有不同的配置项
        switch (prop){
            case ComponentProp.IMG:
                ret.addAll(getAllConfig(ImgEditConfig.values()));
                break;
            case ComponentProp.BAR:
                ret.addAll(getAllConfig(BarEditConfig.values()));
                break;
            case ComponentProp.LINE:
                ret.addAll(getAllConfig(LineEditConfig.values()));
                break;
            case ComponentProp.STEREOSCOPIC_BAR:
                ret.addAll(getAllConfig(Bar2P5DEditConfig.values()));
                break;
            case ComponentProp.TEXT:
                ret.addAll(getAllConfig(TextEditConfig.values()));
                break;
            case ComponentProp.STEREOSCOPIC_PIE:
                ret.addAll(getAllConfig(PIE3DEditConfig.values()));
                break;
            case ComponentProp.PIE:
                ret.addAll(getAllConfig(PIEEditConfig.values()));
                break;
            case ComponentProp.PICTORIAL_BAR:
                ret.addAll(getAllConfig(PictorialBarEditConfig.values()));
                break;
            case ComponentProp.PEAK_BAR:
                ret.addAll(getAllConfig(PeakBarEditConfig.values()));
                break;
            case ComponentProp.RADAR:
                ret.addAll(getAllConfig(RadarEditConfig.values()));
                break;
            case ComponentProp.FUNNEL:
                ret.addAll(getAllConfig(FunnelEditConfig.values()));
                break;
            case ComponentProp.SCATTER:
                ret.addAll(getAllConfig(ScatterEditConfig.values()));
                break;
            case ComponentProp.NEW_PICTORIAl_BAR:
                ret.addAll(getAllConfig(NewPictorialBarEditConfig.values()));
                break;
            case ComponentProp.SCROL_RANKING:
                ret.addAll(getAllConfig(ScrollRankingEditConfig.values()));
                break;
            case ComponentProp.TABLE:
                ret.addAll(getAllConfig(TableEditConfig.values()));
                break;
            default:
                break;
        }
    }

    /**
     * 获取枚举类实例
     * @param instanceS
     * @return
     */
    private static List<VisualSelect> getAllConfig(VisualEdtConfigInterface[] instanceS){
        List<VisualSelect> ret = new ArrayList<>(instanceS.length);
        for (VisualEdtConfigInterface value : instanceS) {
            ret.add(new VisualSelect(value.getName(),value.getConfig()));
        }
        return ret;
    }




}
