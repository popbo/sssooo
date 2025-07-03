package com.stdc.visual.auth.filter;

import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.SpringContextUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.user.CustomUserDetails;
import com.stdc.visual.auth.service.SysUserService;
import com.stdc.visual.common.utils.HttpServletUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: wang_jie
 * @data: 2022/5/23--14:04
 * @describe: token校验过滤器
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = AuthUtils.getToken();
        //没有token 放行 让后面的过滤器进行校验
        if (StringUtil.isBlank(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        // token存在 存入SecurityContextHolder 后续需要验证的话在SpringSecurity过滤器中使用authenticate进行认证
        CustomUserDetails loginUser = AuthUtils.getUserDetails(token);
        //存入SecurityContextHolder 后续在SpringSecurity过滤器中使用authenticate进行认证
        //TODO 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }



}

