package com.stdc.visual.controller.mock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: wang_jie
 * @data: 2024/3/13--9:46
 * @describe:
 */
@Api(tags = "智慧煤矿-电力Mock数据")
@ApiSupport(author = "wangjie",order = 0)
@RestController
@RequestMapping("/mock/smart-minie")
public class MockSmartMineElectricityController {

    @Data
    @NoArgsConstructor
    static
    class Electricity{
        //id
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String id;
        //名称
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String name;
        //value
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Object value;
        //图元子类
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<Electricity> children;

        public Electricity(String id, String name, Object value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public Electricity(String id, String name, Object value, int elementCount) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.children = new ArrayList<>();
            for (int i = 0; i < elementCount; i++) {
                //图元id
                String elementId = id + StringPool.DASH + "element" + StringPool.DASH + (i+1);
                //图元名称
                String elementName = "图元"+(i+1);
                //图元值
                String elementValue = String.valueOf(getIntervalRandomNumber(0,3));
                this.children.add(new Electricity(elementId,elementName,elementValue));
            }
        }
    }

    private static String ELECTRICITY_DEVICE_ID_PREFIX = "smart-mine-device-electricity";

    //电力图标状态
    private static List<Electricity> ELECTRICITYS = new ArrayList<>();

    static {
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "jd","接地",null,44));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "dlq","断路器",null,29));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "blq","避雷器",null,32));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "sxsgcjrzdlhgq","三相三个次级绕组电流互感器",null,34));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "srzbyq","三绕组变压器",null,1));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "srzyzktbyq","双绕组有载可调变压器",null,2));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "ddxsq","带电显示器",null,30));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "wjbhzz","微机保护装置",null,24));
        ELECTRICITYS.add(new Electricity(ELECTRICITY_DEVICE_ID_PREFIX + StringPool.DASH + "byq","变压器",null,4));
    }


    /**
     * s数据
     */
    private static final  String DATA_ARRAY = "[{\"children\":[{\"id\":\"device-000-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-000-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-000-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-000-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-000-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-000-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-000\",\"name\":\"35kV变电站\"},{\"children\":[{\"id\":\"device-001-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-001-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-001-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-001-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-001-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-001-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-001\",\"name\":\"35kV 2#进线\"},{\"children\":[{\"id\":\"device-002-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-002-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-002-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-002-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-002-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-002-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-002\",\"name\":\"35kV 2#PT\"},{\"children\":[{\"id\":\"device-003-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-003-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-003-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-003-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-003-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-003-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-003\",\"name\":\"2#主变\"},{\"children\":[{\"id\":\"device-004-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-004-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-004-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-004-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-004-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-004-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-004\",\"name\":\"3#供热主变高后备\"},{\"children\":[{\"id\":\"device-005-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-005-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-005-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-005-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-005-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-005-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-005\",\"name\":\"1#主变高压后备\"},{\"children\":[{\"id\":\"device-006-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-006-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-006-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-006-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-006-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-006-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-006\",\"name\":\"35kV 1#进线\"},{\"children\":[{\"id\":\"device-007-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-007-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-007-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-007-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-007-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-007-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-007\",\"name\":\"副斜井机房II回\"},{\"children\":[{\"id\":\"device-008-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-008-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-008-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-008-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-008-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-008-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-008\",\"name\":\"2#接地变\"},{\"children\":[{\"id\":\"device-009-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-009-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-009-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-009-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-009-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-009-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-009\",\"name\":\"主皮带II回\"},{\"children\":[{\"id\":\"device-010-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-010-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-010-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-010-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-010-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-010-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-010\",\"name\":\"主扇II回\"},{\"children\":[{\"id\":\"device-011-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-011-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-011-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-011-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-011-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-011-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-011\",\"name\":\"15#下井II回\"},{\"children\":[{\"id\":\"device-012-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-012-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-012-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-012-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-012-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-012-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-012\",\"name\":\"10kV变电站II回\"},{\"children\":[{\"id\":\"device-013-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-013-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-013-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-013-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-013-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-013-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-013\",\"name\":\"380V变压器\"},{\"children\":[{\"id\":\"device-014-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-014-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-014-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-014-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-014-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-014-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-014\",\"name\":\"2#电容器\"},{\"children\":[{\"id\":\"device-015-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-015-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-015-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-015-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-015-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-015-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-015\",\"name\":\"箱变\"},{\"children\":[{\"id\":\"device-016-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-016-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-016-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-016-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-016-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-016-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-016\",\"name\":\"5#备出线\"},{\"children\":[{\"id\":\"device-017-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-017-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-017-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-017-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-017-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-017-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-017\",\"name\":\"3#下井I回\"},{\"children\":[{\"id\":\"device-018-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-017-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-017-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-017-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-017-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-017-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-017\",\"name\":\"副斜井机房I回\"},{\"children\":[{\"id\":\"device-018-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-018-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-018-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-018-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-018-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-018-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-018\",\"name\":\"1#接地变\"},{\"children\":[{\"id\":\"device-019-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-019-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-019-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-019-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-019-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-019-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-019\",\"name\":\"主皮带I回\"},{\"children\":[{\"id\":\"device-020-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-020-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-020-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-020-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-020-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-020-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-020\",\"name\":\"主扇I回\"},{\"children\":[{\"id\":\"device-021-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-021-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-021-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-021-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-021-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-021-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-021\",\"name\":\"15#下井i回\"},{\"children\":[{\"id\":\"device-022-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-022-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-022-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-022-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-022-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-022-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-022\",\"name\":\"10kV变电站I回\"},{\"children\":[{\"id\":\"device-023-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-023-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-023-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-023-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-023-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-023-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-023\",\"name\":\"380V变压器I回\"},{\"children\":[{\"id\":\"device-024-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-024-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-024-c\",\"name\":\"lc\",\"value\":\"\"},{\"id\":\"device-024-d\",\"name\":\"COS\",\"value\":\"\"},{\"id\":\"device-024-e\",\"name\":\"F\",\"value\":\"\"},{\"id\":\"device-024-f\",\"name\":\"Q\",\"value\":\"\"}],\"id\":\"device-024\",\"name\":\"1#电容器\"},{\"children\":[{\"id\":\"device-025-a\",\"name\":\"Ua\",\"value\":\"\"},{\"id\":\"device-025-b\",\"name\":\"Ub\",\"value\":\"\"},{\"id\":\"device-025-c\",\"name\":\"Uc\",\"value\":\"\"},{\"id\":\"device-025-d\",\"name\":\"Uab\",\"value\":\"\"},{\"id\":\"device-025-e\",\"name\":\"Ubc\",\"value\":\"\"},{\"id\":\"device-025-f\",\"name\":\"Uca\",\"value\":\"\"}],\"id\":\"device-025\",\"name\":\"35kV II段母线\"},{\"children\":[{\"id\":\"device-026-a\",\"name\":\"Ua\",\"value\":\"\"},{\"id\":\"device-026-b\",\"name\":\"Ub\",\"value\":\"\"},{\"id\":\"device-026-c\",\"name\":\"Uc\",\"value\":\"\"},{\"id\":\"device-026-d\",\"name\":\"Uab\",\"value\":\"\"},{\"id\":\"device-026-e\",\"name\":\"Ubc\",\"value\":\"\"},{\"id\":\"device-026-f\",\"name\":\"Uca\",\"value\":\"\"}],\"id\":\"device-026\",\"name\":\"35kV I段母线\"},{\"children\":[{\"id\":\"device-027-a\",\"name\":\"Ua\",\"value\":\"\"},{\"id\":\"device-027-b\",\"name\":\"Ub\",\"value\":\"\"},{\"id\":\"device-027-c\",\"name\":\"Uc\",\"value\":\"\"},{\"id\":\"device-027-d\",\"name\":\"Uab\",\"value\":\"\"},{\"id\":\"device-027-e\",\"name\":\"Ubc\",\"value\":\"\"},{\"id\":\"device-027-f\",\"name\":\"Uca\",\"value\":\"\"}],\"id\":\"device-027\",\"name\":\"10kV I段母线\"},{\"children\":[{\"id\":\"device-028-a\",\"name\":\"Ua\",\"value\":\"\"},{\"id\":\"device-028-b\",\"name\":\"Ub\",\"value\":\"\"},{\"id\":\"device-028-c\",\"name\":\"Uc\",\"value\":\"\"},{\"id\":\"device-028-d\",\"name\":\"Uab\",\"value\":\"\"},{\"id\":\"device-028-e\",\"name\":\"Ubc\",\"value\":\"\"},{\"id\":\"device-028-f\",\"name\":\"Uca\",\"value\":\"\"}],\"id\":\"device-028\",\"name\":\"10kV II段母线\"},{\"children\":[{\"id\":\"device-029-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-029-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-029-c\",\"name\":\"lc\",\"value\":\"\"}],\"id\":\"device-029\",\"name\":\"35kV母联\"},{\"children\":[{\"id\":\"device-030-a\",\"name\":\"la\",\"value\":\"\"},{\"id\":\"device-030-b\",\"name\":\"lb\",\"value\":\"\"},{\"id\":\"device-030-c\",\"name\":\"lc\",\"value\":\"\"}],\"id\":\"device-030\",\"name\":\"10kV母联\"},{\"children\":[{\"id\":\"device-031-a\",\"name\":\"挡位\",\"value\":\"\"},{\"id\":\"device-031-b\",\"name\":\"温度\",\"value\":\"\"}],\"id\":\"device-032\",\"name\":\"2号主变压器\"},{\"children\":[{\"id\":\"device-032-a\",\"name\":\"挡位\",\"value\":\"\"},{\"id\":\"device-032-b\",\"name\":\"温度\",\"value\":\"\"}],\"id\":\"device-032\",\"name\":\"1号主变压器\"}]";


    @ApiOperationSupport(order = 1)
    @ApiOperation("电力详情1")
    @GetMapping("/electricity-detail1")
    public Object electricityDetail1(){
        JSONArray dataArray = JSONArray.parseArray(DATA_ARRAY);
        for (Object o : dataArray) {
            JSONObject obj = (JSONObject)o;
            JSONArray children = obj.getJSONArray("children");
            for (Object child : children) {
                ((JSONObject)child).put("value",getRoundToTwoDecimal(0,100));
            }
        }
        return dataArray;
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("电力详情2")
    @GetMapping("/electricity-detail2")
    public Object electricityDetail2(){
        return ELECTRICITYS;
    }

    /**
     *
     * 传入区间，返回2位小数01
     * @param startRange
     * @param endRange
     * @return
     */
    private static double getRoundToTwoDecimal(double startRange, double endRange) {
        double random = Math.random();
        double range = endRange - startRange;
        double scaledRandom = random * range + startRange;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedNumber = decimalFormat.format(scaledRandom);
        return Double.parseDouble(formattedNumber);
    }

    /**
     * 获取区间随机数字
     * @param min
     * @param max
     * @return
     */
    private static int getIntervalRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
