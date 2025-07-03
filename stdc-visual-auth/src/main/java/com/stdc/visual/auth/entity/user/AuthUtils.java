package com.stdc.visual.auth.entity.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.auth.utils.JwtUtil;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.redis.util.RedisUtils;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.SpringContextUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.prop.sso.SsoConfigProperties;
import com.stdc.visual.auth.service.RoleService;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.auth.service.UserRoleService;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.common.utils.HttpServletUtil;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: wang_jie
 * @data: 2022/5/30--19:45
 * @describe: 权限工具类
 */
public class AuthUtils {

    /**
     * 注销黑名单token
     */
    public static final String LOG_OUT_TOKEN_BLACK_LIST = "LOG_OUT_TOKEN_BLACK_LIST:";

    /**
     * 请求头获取token key值
     */
    public static final String AUTHORIZATION = "Authorization";

    /**
     * 发布大屏请求头token key值
     */
    public static final String AUTHORIZATION_RELEASE_PATH = "ReleasePath";

    /**
     * token 前缀
     */
    public static final String TOKEN_HEAD = StdcVisualConstant.PROJECT_PREFIX + "Bearer ";

    /**
     * cas权限时,占位密码,不做比对
     */
    public static final String CAS_DEFAULT_PASSWORD = "cas_default_password";

    //guolin Gl@000925 永久用户
//        String STATIC_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjMzk4MzQzMjhjZTM0NjkzYjYyNDMyNDI0YWY5MTFlOSIsInN1YiI6IntcImFjY291bnROb25FeHBpcmVkXCI6dHJ1ZSxcImFjY291bnROb25Mb2NrZWRcIjp0cnVlLFwiY3JlZGVudGlhbHNOb25FeHBpcmVkXCI6dHJ1ZSxcImVuYWJsZWRcIjp0cnVlLFwicGFzc3dvcmRcIjpcIjZFRENGMzYyQTU5M0U5OENFQkRGNUQ3RUVEQUNDMUVFXCIsXCJ1c2VyXCI6e1wiYWRtaW5cIjowLFwiY3JlYXRlVGltZVwiOjE2MTkwODYwMzYyMzQsXCJkZXB0SWRcIjoxLFwiZW1haWxcIjpcInVuaXR0ZWNALmNvbVwiLFwiZW5hYmxlZFwiOjEsXCJnZW5kZXJcIjpcIuWls1wiLFwibmlja05hbWVcIjpcImd1b2xpblwiLFwicGFzc3dvcmRcIjpcIjZFRENGMzYyQTU5M0U5OENFQkRGNUQ3RUVEQUNDMUVFXCIsXCJyb2xlc1wiOlt7XCJjcmVhdGVUaW1lXCI6MTY0MjQ3NzMyNTQwNyxcImRlc2NyaXB0aW9uXCI6XCJcIixcIm5hbWVcIjpcIuaZrumAmuWRmOW3pVwiLFwicm9sZUlkXCI6Mn1dLFwidXBkYXRlVGltZVwiOjE2MjI1MzM1MDk2OTcsXCJ1c2VySWRcIjo0LFwidXNlcm5hbWVcIjpcImd1b2xpblwifSxcInVzZXJuYW1lXCI6XCJndW9saW5cIn0iLCJpc3MiOiJ1bml0dGVjIiwiaWF0IjoxNjcyODE3NjQ4fQ.zgfu8FJqIdb5FC1MscH_jDQvTBuO4hOYfwRTkpvyk1s";
    //admin 123456 永久用户
//        String STATIC_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3ZGVjYzBiNjk3M2Q0NmM4OWUxZDBmZWIzODdmZTc1NSIsInN1YiI6IntcImFjY291bnROb25FeHBpcmVkXCI6dHJ1ZSxcImFjY291bnROb25Mb2NrZWRcIjp0cnVlLFwiY3JlZGVudGlhbHNOb25FeHBpcmVkXCI6dHJ1ZSxcImVuYWJsZWRcIjp0cnVlLFwicGFzc3dvcmRcIjpcIjFFMEY5NDlFNDY3NEIxQTM4RTkzMTlGRjdEREYzQjJEXCIsXCJ1c2VyXCI6e1wiYWRtaW5cIjoxLFwiZGVwdElkXCI6MCxcImVtYWlsXCI6XCJhZG1pbkBmaXQyY2xvdWQuY29tXCIsXCJlbmFibGVkXCI6MSxcImdlbmRlclwiOlwi55S3XCIsXCJuaWNrTmFtZVwiOlwi566h55CG5ZGYXCIsXCJwYXNzd29yZFwiOlwiMUUwRjk0OUU0Njc0QjFBMzhFOTMxOUZGN0RERjNCMkRcIixcInJvbGVzXCI6W3tcImNyZWF0ZVRpbWVcIjoxNjQyNDc3MzI1NDA3LFwiZGVzY3JpcHRpb25cIjpcIlwiLFwibmFtZVwiOlwi566h55CG5ZGYXCIsXCJyb2xlSWRcIjoxfV0sXCJ1cGRhdGVUaW1lXCI6MTYxNTE4NDk1MTUzNCxcInVzZXJJZFwiOjEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0sXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJpc3MiOiJ1bml0dGVjIiwiaWF0IjoxNjY4Njc0MTc5fQ.-4rkMYUfo7tQboUVxJyZefsbKkH9_HO6sPgC86YIIs0";
    // admin unitytec@000925 永久用户
    private static final String STATIC_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMDNhMWEzNjRiNjk0OTQ0YTkzNzRjMTVlYTE3NzBkYyIsInN1YiI6IntcImFjY291bnROb25FeHBpcmVkXCI6dHJ1ZSxcImFjY291bnROb25Mb2NrZWRcIjp0cnVlLFwiY3JlZGVudGlhbHNOb25FeHBpcmVkXCI6dHJ1ZSxcImVuYWJsZWRcIjp0cnVlLFwicGFzc3dvcmRcIjpcIjc3N0JEMDk3NDQwRDZFMjM5ODFERTZCRTdGRDA0Q0RDXCIsXCJ1c2VyXCI6e1wiYWRtaW5cIjoxLFwiZGVwdElkXCI6XCIwXCIsXCJlbWFpbFwiOlwiYWRtaW5AZml0MmNsb3VkLmNvbVwiLFwiZW5hYmxlZFwiOjEsXCJnZW5kZXJcIjpcIueUt1wiLFwibmlja05hbWVcIjpcIueuoeeQhuWRmFwiLFwicGFzc3dvcmRcIjpcIjc3N0JEMDk3NDQwRDZFMjM5ODFERTZCRTdGRDA0Q0RDXCIsXCJyb2xlc1wiOlt7XCJuYW1lXCI6XCLnrqHnkIblkZhcIixcInJvbGVJZFwiOlwiMVwifV0sXCJ1cGRhdGVUaW1lXCI6MTYxNTE4NDk1MTUzNCxcInVzZXJJZFwiOlwiMVwiLFwidXNlcm5hbWVcIjpcImFkbWluXCJ9LFwidXNlcm5hbWVcIjpcImFkbWluXCJ9IiwiaXNzIjoidW5pdHRlYyIsImlhdCI6MTY5NjczMjc3NH0.75JCKGg7Qz3XZmnuo-fRIh1Vk5L9Nw_GhC7pPsfNfKY";

    /**
     * 获取当前登录SysUserDTO用户
     * @return
     */
    public static SysUserDTO getUser(){
        AuthProperties authProperties = SpringContextUtil.getBean(AuthProperties.class);
        switch (authProperties.getSource()){
            case AuthType.OWN:
                return getLocalUser();
            case AuthType.CAS:
                return getCasUser();
        }
        return null;
    }

    /**
     * 本地用户 SysUserDTO
     */
    public static SysUserDTO getLocalUser(){
        UserRoleService userRoleService = SpringContextUtil.getBean(UserRoleService.class);
        RoleService roleService = SpringContextUtil.getBean(RoleService.class);
        SysUserService sysUserService = SpringContextUtil.getBean(SysUserService.class);
        //获取
        SysUser sysUser = sysUserService.queryOneByUserId(String.valueOf(getUserDetails().getUser().getUserId()));
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtils.copyBean(sysUserDTO,sysUser);

        String userId = sysUserDTO.getUserId().toString();
        //查询角色
        List<UserRolePO> userRolePOS = userRoleService.queryByUserId(userId);
        //根据角色id查询角色详情
        if (!CollectionUtils.isEmpty(userRolePOS)){
            List<RoleDTO> roleDTOS = new ArrayList<>();
            userRolePOS.forEach(userRolePO -> {
                roleDTOS.add(BeanUtils.copyBean(new RoleDTO(), roleService.queryByRoleId(userRolePO.getRoleId())));
            });
            sysUserDTO.setRoles(roleDTOS);
        }
        return sysUserDTO;
    }

    /**
     * 本地用户 SysUserDTO
     */
    public static SysUserDTO getUserByName(String username){
        UserRoleService userRoleService = SpringContextUtil.getBean(UserRoleService.class);
        RoleService roleService = SpringContextUtil.getBean(RoleService.class);
        SysUserService sysUserService = SpringContextUtil.getBean(SysUserService.class);
        //获取
        SysUser sysUser = sysUserService.queryOneByUserName(username);
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtils.copyBean(sysUserDTO,sysUser);

        String userId = sysUserDTO.getUserId().toString();
        //查询角色
        List<UserRolePO> userRolePOS = userRoleService.queryByUserId(userId);
        //根据角色id查询角色详情
        if (!CollectionUtils.isEmpty(userRolePOS)){
            List<RoleDTO> roleDTOS = new ArrayList<>();
            userRolePOS.forEach(userRolePO -> {
                roleDTOS.add(BeanUtils.copyBean(new RoleDTO(), roleService.queryByRoleId(userRolePO.getRoleId())));
            });
            sysUserDTO.setRoles(roleDTOS);
        }
        return sysUserDTO;
    }


    /**
     * 统一认证童虎 SysUserDTO
     */
    public static SysUserDTO getCasUser(){
        RoleService roleService = SpringContextUtil.getBean(RoleService.class);
        //获取用户信息
        SysUserDTO sysUserDTO = getUserDetails().getUser();
        //查询角色详情
        List<RoleDTO> roleDTOS = roleService.queryByUser(sysUserDTO);
        sysUserDTO.setRoles(roleDTOS);
        return sysUserDTO;
    }

    /**
     * 获取当前登录UserDetails用户
     * @return
     */
    public static CustomUserDetails getUserDetails(){
        String token = getToken();
        return getUserDetails(token);
    }
    public static CustomUserDetails getUserDetails(String token){
        //解析token
        CustomUserDetails userDetail = new CustomUserDetails();
        try {
            if (StringUtil.isEmpty(token)) {
                BaseException.throwException(ResultCode.get("Illegal_token"));
            }
            Claims claims = JwtUtil.parseJWT(token);
            SysUserDTO user = JSON.toJavaObject(JSONObject.parseObject(JSONObject.parseObject(claims.getSubject()).getString("user")), SysUserDTO.class);
            userDetail.setUser(user);
//            userDetail = JsonUtil.toJavaObj(claims.getSubject(), CustomUserDetails.class);
            //token 过期
        }catch (ExpiredJwtException e){
            BaseException.throwException(ResultCode.get("login_status_expired"));
        } catch (Exception e) {
            BaseException.throwException(ResultCode.get("Illegal_token"));
        }
        return userDetail;
    }

    /**
     * 获取token
     * @return
     */
    public static String getToken(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //当前请求为发布请求时 ,使用自己适应生成的token
        if (StringUtil.hasText(request.getHeader(AUTHORIZATION_RELEASE_PATH))){
            return getForeverJWT(request.getHeader(AUTHORIZATION_RELEASE_PATH));
        }
        //获取token
        String token = request.getHeader(AUTHORIZATION);
        if (StringUtil.hasText(token) && checkToken(token)){
            return token.replaceAll(TOKEN_HEAD, "");
        }else {
            return null;
        }
    }

    /**
     * 获取黑名单键值key
     * @param token
     * @return
     */
    public static String getBlackTokenKey(String token){
        return LOG_OUT_TOKEN_BLACK_LIST.concat(token);
    }


    /**
     * 获取token
     * @param path)
     * @return
     */
    public static String getForeverJWT(String path) {
        //查询地址是否发布
        SysUserService sysUserService = SpringContextUtil.getBean(SysUserService.class);
        String username = sysUserService.queryUsernameByReleasePath(path);
        if (!StringUtil.hasText(username)){
            BaseException.throwException(ResultCode.get("this_release_path_is_not_release_or_not_exit"));
        }
        //获取用户信息
        CustomUserDetails loginUser = new CustomUserDetails();
        AuthProperties authProperties = SpringContextUtil.getBean(AuthProperties.class);
        switch (authProperties.getSource()){
            case AuthType.OWN:
                SysUser user = sysUserService.queryOneByUserName(username);
                if (ObjectUtil.isEmpty(user)){
                    return null;
                }
                //使用userId生成token
                SysUserDTO sysUserDTO = new SysUserDTO();
                BeanUtils.copyBean(sysUserDTO,user);
                loginUser.setUser(sysUserDTO);
                String userId = sysUserDTO.getUserId().toString();
                //查询角色
                List<UserRolePO> userRolePOS = SpringContextUtil.getBean(UserRoleService.class).queryByUserId(userId);
                //根据角色id查询角色详情
                if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(userRolePOS)){
                    List<RoleDTO> roleDTOS = new ArrayList<>();
                    userRolePOS.forEach(userRolePO -> {
                        RolePO rolePO = SpringContextUtil.getBean(RoleService.class).queryByRoleId(userRolePO.getRoleId());
                        RoleDTO roleDTO = BeanUtils.copyBean(new RoleDTO(), rolePO);
                        roleDTOS.add(roleDTO);
                    });
                    sysUserDTO.setRoles(roleDTOS);
                }
                break;
            case AuthType.CAS:
                RedisUtils redisUtil = SpringContextUtil.getBean(RedisUtils.class);
                SysUser casUser = JsonUtil.toJavaObj((String) redisUtil.getCacheObject(StdcVisualConstant.PROJECT_PREFIX + "RELEASE_VISUAL_USERNAME_" + username), SysUser.class);
                //使用userId生成token
                SysUserDTO sysUserDtoCas = new SysUserDTO();
                BeanUtils.copyBean(sysUserDtoCas,casUser);
                //通过cas用户名获取cas用户的最新的角色
                List<RoleDTO> roleDTOS = SpringContextUtil.getBean(RoleService.class).queryByUser(casUser);
                sysUserDtoCas.setRoles(roleDTOS);
                //设置cas用户
                loginUser.setUser(sysUserDtoCas);
                break;
        }
//        //获取用户信息
//        SysUserService sysUserService = SpringContextUtil.getBean(SysUserService.class);
//        String username = sysUserService.queryUsernameByReleasePath(path);
//        if (!StringUtil.hasText(username)){
//            return null;
//        }

        // 生成永久token
        return JwtUtil.createForeverJWT(JSON.toJSONString(loginUser));
    }

    public static String getForeverJWTForUser(String username) {
        //查询地址是否发布
        SysUserService sysUserService = SpringContextUtil.getBean(SysUserService.class);
        if (!StringUtil.hasText(username)){
            return null;
        }
        //获取用户信息
        CustomUserDetails loginUser = new CustomUserDetails();
        AuthProperties authProperties = SpringContextUtil.getBean(AuthProperties.class);
        switch (authProperties.getSource()){
            case AuthType.OWN:
                SysUser user = sysUserService.queryOneByUserName(username);
                if (ObjectUtil.isEmpty(user)){
                    return null;
                }
                //使用userId生成token
                SysUserDTO sysUserDTO = new SysUserDTO();
                BeanUtils.copyBean(sysUserDTO,user);
                loginUser.setUser(sysUserDTO);
                String userId = sysUserDTO.getUserId().toString();
                //查询角色
                List<UserRolePO> userRolePOS = SpringContextUtil.getBean(UserRoleService.class).queryByUserId(userId);
                //根据角色id查询角色详情
                if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(userRolePOS)){
                    List<RoleDTO> roleDTOS = new ArrayList<>();
                    userRolePOS.forEach(userRolePO -> {
                        RolePO rolePO = SpringContextUtil.getBean(RoleService.class).queryByRoleId(userRolePO.getRoleId());
                        RoleDTO roleDTO = BeanUtils.copyBean(new RoleDTO(), rolePO);
                        roleDTOS.add(roleDTO);
                    });
                    sysUserDTO.setRoles(roleDTOS);
                }
                break;
            case AuthType.CAS:
                loginUser.setUser(getCasUser());
                break;
        }
//        //获取用户信息
//        SysUserService sysUserService = SpringContextUtil.getBean(SysUserService.class);
//        String username = sysUserService.queryUsernameByReleasePath(path);
//        if (!StringUtil.hasText(username)){
//            return null;
//        }

        // 生成永久token
        return JwtUtil.createForeverJWT(JSON.toJSONString(loginUser));
    }

    /**
     * 验证token是否过期,若未过期则刷新token
     * @return
     */
    private static final boolean checkToken(String token){
        RedisUtils redisUtil = SpringContextUtil.getBean(RedisUtils.class);
        Object cacheObject = redisUtil.getCacheObject(token);
        if (ObjectUtil.isEmpty(cacheObject)){
            return false;
        }
        //刷新token过期时间
        AuthProperties authProperties = SpringContextUtil.getBean(AuthProperties.class);
        redisUtil.expire(token,Long.valueOf(authProperties.getTimeout()), TimeUnit.MILLISECONDS);
        return true;
    }

}
