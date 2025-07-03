package com.stdc.visual.controller.visual;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.ws.VisualWebSocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2023/3/30--11:21
 * @describe: 提供给第三方操作组件的http控制器
 */
@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("/api")
@ApiSupport(author = "wangjie",order = 12)
@Api(value = "提供给第三方操作组件时间的http控制器", tags = "提供给第三方操作组件时间的http控制器")
public class VisualApiController {

    @Value("${python.api}")
    private String api;

    //提供给第三方的http接口地址
    private static final String API_HTTP_URL = "http://{ip}:{port}/stdc-visual/api/events/{apiId}";

    //提供给第三方的ws接口地址
    private static final String API_WS_URL = "ws://{ip}:{port}/stdc-visual/api/{apiId}";

    @GetMapping("/register")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "前端获取api接口", notes = "前端获取api接口")
    public R register(@ApiParam(value = "事件id", required = true) String eventId,
                      @ApiParam(value = "api类型,http/ws", required = true) String type) throws UnknownHostException {
        String url = null;
        String ip = api.replaceAll("http://","").split(":")[0];
        String port = api.replaceAll("http://","").split(":")[1];;
        if (StringUtil.equals("http",type)){
            url = API_HTTP_URL;
        }else if (StringUtil.equals("ws",type)){
            url = API_WS_URL;
        }else {
            return R.fail("type error");
        }
        url = url.replaceAll("\\{ip\\}", ip).replaceAll("\\{port\\}", port).replaceAll("\\{apiId\\}", eventId);
        return R.data(url);
    }


    @GetMapping("/events/{apiId}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "第三方控制可视化事件", notes = "第三方控制可视化事件")
    public R events(@PathVariable("apiId") String apiId){
        Map<String,String> msg = new HashMap<>();
        msg.put("eventId",apiId);
        VisualWebSocket.sendMessage(msg);
        return R.success("");
    }
}
