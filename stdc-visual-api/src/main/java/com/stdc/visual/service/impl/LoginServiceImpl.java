package com.stdc.visual.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.prop.AuthType;
import com.stdc.core.auth.utils.JwtUtil;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.redis.util.RedisUtils;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.SpringContextUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.RoleDTO;
import com.stdc.visual.auth.entity.role.po.RolePO;
import com.stdc.visual.auth.entity.role.po.UserRolePO;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.CustomUserDetails;
import com.stdc.visual.auth.entity.user.dto.SysUserDTO;
import com.stdc.visual.auth.entity.user.po.SysUser;
import com.stdc.visual.auth.service.RoleService;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.auth.service.UserRoleService;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.common.utils.JsonUtil;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.service.LoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.stdc.visual.auth.entity.user.AuthUtils.LOG_OUT_TOKEN_BLACK_LIST;

/**
 * @author: wang_jie
 * @data: 2022/5/23--11:09
 * @describe:
 */
@Slf4j
@Service("LoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private SsoService ssoService;

    @Autowired
    private SysUserService service;


    @Override
    public R login(SysUser user) {
        if (StringUtil.equalsIgnoreCase(AuthType.CAS,authProperties.getSource())){
            BaseException.throwException("当前登录配置为CAS单点登录");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(ObjectUtil.isEmpty(authenticate)){
            BaseException.throwException(ResultCode.get("username_or_password_error"));
        }
        //使用userId生成token
        CustomUserDetails loginUser = (CustomUserDetails) authenticate.getPrincipal();
        SysUserDTO sysUserDTO = loginUser.getUser();
        String userId = sysUserDTO.getUserId().toString();
        //查询角色
        List<UserRolePO> userRolePOS = userRoleService.queryByUserId(userId);
        //根据角色id查询角色详情
        if (CollectionUtils.isNotEmpty(userRolePOS)){
            List<RoleDTO> roleDTOS = new ArrayList<>();
            userRolePOS.forEach(userRolePO -> {
                RolePO rolePO = roleService.queryByRoleId(userRolePO.getRoleId());
                RoleDTO roleDTO = BeanUtils.copyBean(new RoleDTO(), rolePO);
                roleDTOS.add(roleDTO);
            });
            sysUserDTO.setRoles(roleDTOS);
        }
        //获取token
        String token = JwtUtil.createForeverJWT(JSON.toJSONString(loginUser));
        loginUser.getUser().setToken(token);
        //把token响应给前端
        HashMap<String,Object> map = new LinkedHashMap<>();
        map.put("token",  AuthUtils.TOKEN_HEAD + token);
        //返回用户信息
        loginUser.getUser().setPassword("******");
        BeanUtils.copyBean(user,loginUser.getUser());
        JSONObject userJson = JSONObject.parseObject(JSON.toJSONString(user));
        userJson.put("extend",service.queryExtendByUserName(user.getUsername()));
        map.put("user",userJson);
        // 设置默认token有效时间 2 小时
        Long aLong = Long.valueOf(authProperties.getTimeout());
        redisUtils.setCacheObject( AuthUtils.TOKEN_HEAD + token, AuthUtils.TOKEN_HEAD + token,aLong,TimeUnit.MILLISECONDS);
        return R.data(map);
    }

    @Override
    public R loginForTopology(String userName) {
        SysUser sysUser = service.queryOneByUserName(userName);
        if (ObjectUtil.isEmpty(sysUser)){
            return R.fail("用户不存在");
        }
        return login(sysUser);
    }

    @Override
    public R login(String ticket) {
        if (StringUtil.equalsIgnoreCase(AuthType.OWN,authProperties.getSource())){
            BaseException.throwException("当前登录配置为可视化登录");
        }
        //通过ticket获取到用户信息
        SysUser user = ssoService.getUserInfoByTicket(ticket, null);
        CustomUserDetails loginUser = new CustomUserDetails();
        SysUserDTO sysUserDTO = BeanUtils.copyBean(new SysUserDTO(),user);
        loginUser.setUser(sysUserDTO);
        List<RoleDTO> roleDTOS = roleService.queryByUser(user);
        sysUserDTO.setRoles(roleDTOS);
        String token = JwtUtil.createForeverJWT(JSON.toJSONString(loginUser));
        loginUser.getUser().setToken(token);
        //把token响应给前端
        HashMap<String,Object> map = new LinkedHashMap<>();
        map.put("token",  AuthUtils.TOKEN_HEAD + token);
        //返回用户信息
        BeanUtils.copyBean(user,loginUser.getUser());
        user.setPassword("******");
        map.put("user",user);
        // 设置默认token有效时间 2 小时
        Long aLong = Long.valueOf(authProperties.getTimeout());
        redisUtils.setCacheObject( AuthUtils.TOKEN_HEAD + token, AuthUtils.TOKEN_HEAD + token,aLong,TimeUnit.MILLISECONDS);
        //设置cas用户的发布的用户信息 只存用户名和用户id
        redisUtils.setCacheObject(StdcVisualConstant.PROJECT_PREFIX + "RELEASE_VISUAL_USERNAME_" + user.getUsername(),JSON.toJSONString(user));
        return R.data(map);
    }

    @Override
    public R logOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        CustomUserDetails loginUser = (CustomUserDetails) principal;
        String token = loginUser.getUser().getToken();
        //设置token黑名单
        redisUtils.setCacheObject(AuthUtils.getBlackTokenKey(token),LOG_OUT_TOKEN_BLACK_LIST,Long.valueOf(authProperties.getTimeout()) * 2, TimeUnit.MILLISECONDS);
        authentication.setAuthenticated(false);//设置未验证
        return R.data(HttpServletResponse.SC_OK,"注销成功");
    }

    @Override
    public R refreshToken(String refreshToken) {
        SysUser sysUser = null;
        try {
            if (StringUtil.isEmpty(refreshToken)) {
                BaseException.throwException(ResultCode.get("Illegal_token"));
            }
            Claims claims = JwtUtil.parseJWT(refreshToken);
            sysUser = JsonUtil.toJavaObj(claims.getSubject(), SysUser.class);
        //refreshToken 过期
        }catch (ExpiredJwtException e){
            return R.fail(HttpServletResponse.SC_UNAUTHORIZED,ResultCode.get("login_status_expired"));
        } catch (Exception e) {
            return R.fail(HttpServletResponse.SC_UNAUTHORIZED,ResultCode.get("refresh_token_error"));
        }
        return login(sysUser);
    }

    @Override
    public R check(String token) {
        Object cacheObject = redisUtils.getCacheObject(token);
        if (ObjectUtil.isEmpty(cacheObject)){
            return R.fail(ResultCode.get("login_status_expired"));
        }
        return R.success("用户已登录");
    }
}
