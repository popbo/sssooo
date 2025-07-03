package com.stdc.visual.service.impl;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.prop.sso.SsoConfigProperties;
import com.stdc.visual.auth.utils.JsonUtil;
import com.stdc.visual.auth.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: wang_jie
 * @data: 2022/11/11--11:02
 * @describe:
 */

@Component
@Slf4j
public class SsoService {

   @Autowired
   private SsoConfigProperties properties;

    public SysUser getUserInfoByTicket(String ticket, String service) {
        String casUserDetail = "";
        String errorCode = "";
        SysUser sysUser = new SysUser();
        try {
            String loginUrl = String.format("%s/p3/serviceValidate?ticket=%s&service=%s", properties.getServiceUrl(), ticket, service);
            casUserDetail = HttpUtil.get(loginUrl);
            /*casUserDetail = "<cas:serviceResponse xmlns:cas=\"http://www.yale.edu/tp/cas\"><cas:authenticationSuccess><cas:user>admin</cas:user><cas:attributes><cas:birthday>null</cas:birthday>\n" +
                    "<cas:gender>1</cas:gender>\n" +
                    "<cas:displayName>base64:57O757uf566h55CG5ZGY</cas:displayName>\n" +
                    "<cas:departmentId>105</cas:departmentId>\n" +
                    "<cas:mobile>15618726256</cas:mobile>\n" +
                    "<cas:title>base64:57O757uf566h55CG5ZGY</cas:title>\n" +
                    "<cas:uuid>765441c4-f787-402d-ae12-5e086477cb5c</cas:uuid>\n" +
                    "<cas:online_ticket>OT810583531649499136</cas:online_ticket>\n" +
                    "<cas:lastname>base64:YWRtaW4=</cas:lastname>\n" +
                    "<cas:employeeNumber>2000002</cas:employeeNumber>\n" +
                    "<cas:uid>1</cas:uid>\n" +
                    "<cas:firstName>base64:YWRtaW4=</cas:firstName>\n" +
                    "<cas:institution>1</cas:institution>\n" +
                    "<cas:workRegion>base64:5YyX5Lqs</cas:workRegion>\n" +
                    "<cas:department>base64:56eR5oqA6YOo</cas:department>\n" +
                    "<cas:email>admin@QxzyC82JWA-mail.com</cas:email>\n" +
                    "</cas:attributes></cas:authenticationSuccess></cas:serviceResponse>";
            casUserDetail = casUserDetail.replace("\\\"", "\"");*/
            String json = XmlUtil.toJson(casUserDetail);
            JsonNode jsonNode = JsonUtil.toJsonNode(json);
            if (jsonNode.has("authenticationSuccess")) {
                JsonNode userInfo = jsonNode.get("authenticationSuccess").get("attributes");
                sysUser.setUserId(userInfo.get("uid").asText());
                sysUser.setUsername(jsonNode.get("authenticationSuccess").get("user").asText());
                sysUser.setEmail(userInfo.get("email").asText());
//                map.put("uuid", userInfo.get("uuid").asText());
            } else if (jsonNode.has("authenticationFailure")) {
                errorCode = jsonNode.get("authenticationFailure").get("code").asText();
                BaseException.throwException("cas认证失败："+errorCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取cas认证信息失败：" + casUserDetail);
            BaseException.throwException("获取cas认证信息失败： " + e.getMessage());
        }
        return sysUser;
    }
}
