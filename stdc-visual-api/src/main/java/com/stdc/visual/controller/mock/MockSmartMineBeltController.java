package com.stdc.visual.controller.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.redis.util.RedisUtils;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

/**
 * @author: wang_jie
 * @data: 2024/3/13--9:46
 * @describe:
 */
@Api(tags = "智慧煤矿-皮带Mock数据")
@ApiSupport(author = "wangjie",order = 0)
@RestController
@RequestMapping("/mock/smart-minie")
public class MockSmartMineBeltController {

    /**
     * 皮带缓存KEY
     */
    private static List<Belt> STATIC_BELT_DETAILS = new ArrayList<>();

    //皮带实体类
    @Data
    @AllArgsConstructor
    class Belt implements Serializable{
        //名称
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String name;
        //设备id
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String id;
        //value值
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Object value;
        //子类
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Belt> children;
    }

    //面板实体类
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Panel{
        //名称
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String name;
        //速度
        private String speed;
        //是否报警
        private Integer isAlarm;
        //皮带温度
        private String beltTemp;
        //前轴温度
        private String frontTemp;
        //后轴温度
        private String backTemp;
        //绕组A温度
        private String windTempA;
        //绕组B温度
        private String windTempB;
        //绕组C温度
        private String windTempC;
        //报警原因
        private String alarmMsg;

        @Override
        public String toString() {
            return "{" +
                    "name:'" + name + '\'' +
                    ", speed:'" + speed + '\'' +
                    ", isAlarm:" + isAlarm +
                    ", beltTemp:'" + beltTemp + '\'' +
                    ", frontTemp:'" + frontTemp + '\'' +
                    ", backTemp:'" + backTemp + '\'' +
                    ", windTempA:'" + windTempA + '\'' +
                    ", windTempB:'" + windTempB + '\'' +
                    ", windTempC:'" + windTempC + '\'' +
                    ", alarmMsg:'" + alarmMsg + '\'' +
                    '}';
        }
    }



    //颜色对应值 灰:AAAAAA 红:F44354 黄:F9EC48 蓝:18D8FF 绿:17EA6B
    private static String[] BELT_COLORS = {"#AAAAAA","#F44354","#F9EC48","#18D8FF","#17EA6B"};



    //一号皮带id-color
    private static String BELT_ONE_ID = "smart-mine-device-belt-one";
    //一号皮带id-color
    private static String BELT_ONE_COLOR_ID = "smart-mine-device-belt-one-color";
    //一号皮带id-status
    private static String BELT_ONE_STATUS_ID = "smart-mine-device-belt-one-status";
    //一号皮带id-status
    private static String BELT_ONE_PANEL_ID = "smart-mine-device-belt-one-panel";

    //二号皮带id
    private static String BELT_TWO_ID = "smart-mine-device-belt-two";
    //二号皮带id
    private static String BELT_TWO_COLOR_ID = "smart-mine-device-belt-two-color";
    //二号皮带id
    private static String BELT_TWO_STATUS_ID = "smart-mine-device-belt-two-status";
    //二号皮带id
    private static String BELT_TWO_PANEL_ID = "smart-mine-device-belt-two-panel";

    //三号皮带id
    private static String BELT_THREE_ID = "smart-mine-device-belt-three";
    //三号皮带id
    private static String BELT_THREE_COLOR_ID = "smart-mine-device-belt-three-color";
    //三号皮带id
    private static String BELT_THREE_STATUS_ID = "smart-mine-device-belt-three-status";
    //三号皮带id
    private static String BELT_THREE_PANEL_ID = "smart-mine-device-belt-three-panel";

    //四号皮带id
    private static String BELT_FOUR_ID = "smart-mine-device-belt-four";
    //四号皮带id
    private static String BELT_FOUR_COLOR_ID = "smart-mine-device-belt-four-color";
    //四号皮带id
    private static String BELT_FOUR_STATUS_ID = "smart-mine-device-belt-four-status";
    //四号皮带id
    private static String BELT_FOUR_PANEL_ID = "smart-mine-device-belt-four-panel";

    //主斜井皮带id
    private static String BELT_ZXJ_ID = "smart-mine-device-belt-zxj";
    //主斜井皮带id
    private static String BELT_ZXJ_COLOR_ID = "smart-mine-device-belt-zxj-color";
    //主斜井皮带id
    private static String BELT_ZXJ_STATUS_ID = "smart-mine-device-belt-zxj-status";
    //主斜井皮带id
    private static String BELT_ZXJ_PANEL_ID = "smart-mine-device-belt-zxj-panel";

    //给煤机皮带id
    private static String BELT_GMJ_ID = "smart-mine-device-belt-gmj";
    //给煤机皮带id
    private static String BELT_GMJ_COLOR_ID = "smart-mine-device-belt-gmj-color";
    //给煤机皮带id
    private static String BELT_GMJ_STATUS_ID = "smart-mine-device-belt-gmj-status";

    @ApiOperationSupport(order = 1)
    @ApiOperation("皮带列表详情")
    @GetMapping("/belt-details")
    public Object beltDetails(){
        if (CollectionUtils.isNotEmpty(STATIC_BELT_DETAILS)){
            return STATIC_BELT_DETAILS;
        }
        STATIC_BELT_DETAILS.clear();
        initDetails("一号皮带",STATIC_BELT_DETAILS);
        initDetails("二号皮带",STATIC_BELT_DETAILS);
        initDetails("三号皮带",STATIC_BELT_DETAILS);
        initDetails("四号皮带",STATIC_BELT_DETAILS);
        initDetails("主斜井皮带",STATIC_BELT_DETAILS);
        initDetails("给煤机皮带",STATIC_BELT_DETAILS);
        return STATIC_BELT_DETAILS;
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("皮带开关状态详情")
    @GetMapping("/belt-on-off")
    public Object beltOnOff(){
        List<Belt> beltDetails = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(STATIC_BELT_DETAILS)){
            for (Belt beltDetail : STATIC_BELT_DETAILS) {
                Belt status1 = beltDetail.getChildren().stream().filter(belt -> belt.getId().contains("status")).findFirst().get();
                Object status = status1.getValue();
                beltDetail.setValue(status);
                beltDetails.add(new Belt(beltDetail.name,beltDetail.id,beltDetail.value,null));
            }
            return beltDetails;
        }
        return null;
    }

    /**
     * 通过名称获取皮带设备模拟详情
     * @param deviceName
     * @return
     */
    private void initDetails(String deviceName,List<Belt> details){
        String deviceId = null;
        String deviceColorId = null;
        String deviceStatusId = null;
        String devicePanelId = null;
        switch (deviceName){
            case "一号皮带":
                deviceId = BELT_ONE_ID;
                deviceColorId = BELT_ONE_COLOR_ID;
                deviceStatusId = BELT_ONE_STATUS_ID;
                devicePanelId = BELT_ONE_PANEL_ID;
                break;
            case "二号皮带":
                deviceId = BELT_TWO_ID;
                deviceColorId = BELT_TWO_COLOR_ID;
                deviceStatusId = BELT_TWO_STATUS_ID;
                devicePanelId = BELT_TWO_PANEL_ID;
                break;
            case "三号皮带":
                deviceId = BELT_THREE_ID;
                deviceColorId = BELT_THREE_COLOR_ID;
                deviceStatusId = BELT_THREE_STATUS_ID;
                devicePanelId = BELT_THREE_PANEL_ID;
                break;
            case "四号皮带":
                deviceId = BELT_FOUR_ID;
                deviceColorId = BELT_FOUR_COLOR_ID;
                deviceStatusId = BELT_FOUR_STATUS_ID;
                devicePanelId = BELT_FOUR_PANEL_ID;
                break;
            case "主斜井皮带":
                deviceId = BELT_ZXJ_ID;
                deviceColorId = BELT_ZXJ_COLOR_ID;
                deviceStatusId = BELT_ZXJ_STATUS_ID;
                devicePanelId = BELT_ZXJ_PANEL_ID;
                break;
            default:
                break;
        }
        if (StringUtil.equals("给煤机皮带",deviceName)){
            List<Belt> child = new ArrayList<>();
            child.add(new Belt("颜色" ,BELT_GMJ_COLOR_ID,"#17EA6B",null));
            child.add(new Belt("状态" ,BELT_GMJ_STATUS_ID,getRandomStatus08(),null));
            //面板参数
            Panel panel = new Panel();
            panel.setName(deviceName);
            panel.setSpeed(String.valueOf(getIntervalRandomNumber(12,20)));
            panel.setIsAlarm(0);
            panel.setBeltTemp(String.valueOf(getIntervalRandomNumber(50,100)));
            panel.setFrontTemp(String.valueOf(getIntervalRandomNumber(50,100)));
            panel.setBackTemp(String.valueOf(getIntervalRandomNumber(50,100)));
            panel.setWindTempA(String.valueOf(getIntervalRandomNumber(50,100)));
            panel.setWindTempB(String.valueOf(getIntervalRandomNumber(50,100)));
            panel.setWindTempC(String.valueOf(getIntervalRandomNumber(50,100)));
            panel.setAlarmMsg("");
            child.add(new Belt("面板参数" ,devicePanelId,panel,null));
            details.add(new Belt(deviceName,BELT_GMJ_ID,null,child));
        }else {
            List<Belt> child = new ArrayList<>();
            String randomColor = getRandomColor();
            child.add(new Belt("颜色" ,deviceColorId,randomColor,null));
            //绿色
            if (StringUtil.equals("#17EA6B",randomColor)){
                //面板参数
                Panel panel = new Panel();
                panel.setName(deviceName);
                panel.setSpeed(String.valueOf(getIntervalRandomNumber(12,20)));
                panel.setIsAlarm(0);
                panel.setBeltTemp(String.valueOf(getIntervalRandomNumber(50,100)));
                panel.setFrontTemp(String.valueOf(getIntervalRandomNumber(50,100)));
                panel.setBackTemp(String.valueOf(getIntervalRandomNumber(50,100)));
                panel.setWindTempA(String.valueOf(getIntervalRandomNumber(50,100)));
                panel.setWindTempB(String.valueOf(getIntervalRandomNumber(50,100)));
                panel.setWindTempC(String.valueOf(getIntervalRandomNumber(50,100)));
                panel.setAlarmMsg("");
                child.add(new Belt("面板参数" ,devicePanelId,panel,null));
                child.add(new Belt("状态" ,deviceStatusId,1,null));
            }else {
                //面板参数
                Panel panel = new Panel();
                panel.setName(deviceName);
                panel.setSpeed(String.valueOf(0));
                panel.setIsAlarm(1);
                panel.setBeltTemp(String.valueOf(getIntervalRandomNumber(10,20)));
                panel.setFrontTemp(String.valueOf(getIntervalRandomNumber(10,20)));
                panel.setBackTemp(String.valueOf(getIntervalRandomNumber(10,20)));
                panel.setWindTempA(String.valueOf(getIntervalRandomNumber(10,20)));
                panel.setWindTempB(String.valueOf(getIntervalRandomNumber(10,20)));
                panel.setWindTempC(String.valueOf(getIntervalRandomNumber(10,20)));
                //灰:AAAAAA 红:F44354 黄:F9EC48 蓝:18D8FF   灰-离线(默认）黄-异常 红-报警 蓝-通讯异常 模型不会动
                switch (randomColor){
                    case "#AAAAAA":
                        panel.setAlarmMsg("离线");
                        break;
                    case "#F44354":
                        panel.setAlarmMsg("报警");
                        break;
                    case "#F9EC48":
                        panel.setAlarmMsg("异常");
                        break;
                    case "#18D8FF":
                        panel.setAlarmMsg("通讯异常");
                        break;
                }
                child.add(new Belt("面板参数" ,devicePanelId,panel,null));
                child.add(new Belt("状态" ,deviceStatusId,0,null));
            }
            details.add(new Belt(deviceName,deviceId,null,child));
        }
    }


    /**
     * 随机获取颜色
     * @return
     */
    private static String getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(BELT_COLORS.length);
        return BELT_COLORS[index];
    }

    /**
     * 随机获取状态 0 或 1
     * @return
     */
    private static Integer getRandomStatus01() {
        Random random = new Random();
        return random.nextInt(2);
    }

    /**
     * 随机获取状态 0 或 1
     * @return
     */
    private static Integer getRandomStatus08() {
        Random random = new Random();
        return random.nextInt(9);
    }

    /**
     * 获取区间随机数字
     * @param min
     * @param max
     * @return
     */
    public static int getIntervalRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 深拷贝
     * @param object
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T deepCopy(T object) throws IOException, ClassNotFoundException {
        if (ObjectUtil.isEmpty(object)){
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }
}
