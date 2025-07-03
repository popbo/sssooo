package com.stdc.visual.auth.config;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.auth.utils.VisualPasswordEncoder;
import com.stdc.core.redis.util.RedisUtils;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.filter.CustomAccessDenied;
import com.stdc.visual.auth.filter.CustomAuthenticationFailureHandler;
import com.stdc.visual.auth.filter.JwtAuthenticationTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author: wang_jie
 * @data: 2022/5/23--11:39
 * @describe: 权限配置
 */
@Slf4j
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAccessDenied customAccessDenied;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;


    @Autowired
    private AuthProperties authProperties;

    @Autowired
    private RedisUtils redisUtils;

    // url 开放白名单
    public static final String[] URL_WHITE_LIST = {"/visual/project/replaceAll/temporary",
            "/auth/user/login","/auth/cas/login","/auth/refresh-token","/auth/check","/css/**/font.css","/api/events/**","/api/**","/ws/event","/ws/*",
            "/mock/**","/oss/file/get/font-css",
            "/auth/topology/3D/login",
            "/auth/user/login","/auth/cas/login","/auth/refresh-token",
            "/auth/check","/css/**/font.css","/api/events/**","/api/**","/ws/*","/mock/**",
            "/visual/light-weight/topology/export","/oss/level2/excel/export",
            "/topology/data/topo2d/getByPath"};

    // swagger 开放白名单
    public static final String[] SWAGGER_URL = {"/swagger-ui.html","/swagger-resources/**","/doc.html","/doc.html/*","/webjars/**","/v2/**","/*.ico","/api/**"};

    /**
     * 清楚缓存token
     */
    @PostConstruct
    public void refreshToken(){
        Collection<String> keys = redisUtils.keys(AuthUtils.TOKEN_HEAD.concat("*"));
        for (String key : keys) {
            boolean deleteObject = redisUtils.deleteObject(key);
            log.info("清楚缓存token:"+deleteObject+"-->"+key);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //支持跨域访问
        http = http.cors().and();
        if (authProperties.getEnable()){
            http
                    //关闭csrf
                    .csrf().disable()
                    //解决跨域问题
                    .cors()
                    .and()
                    //不通过Session获取SecurityContext
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    // 对于白名单接口 允许访问
                    .antMatchers(URL_WHITE_LIST).permitAll()
                    //对于swagger静态文件 放行
                    .antMatchers(SWAGGER_URL).permitAll()
                    // 除上面外的所有请求全部需要鉴权认证
                    .anyRequest().authenticated();
            //把token校验过滤器添加到过滤器链中
            http.addFilterBefore(new JwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
            //403页面，无权限跳转
            http.exceptionHandling().accessDeniedPage("/403");
            //自定义异常捕获
            http.exceptionHandling()
                    // 自定义未授权异常
                    .accessDeniedHandler(customAccessDenied)
                    // 自定义未登录异常
                    .authenticationEntryPoint(customAuthenticationFailureHandler);
        }else {
            //未开启权限，放行所有请求
            http.csrf().disable().authorizeRequests().anyRequest().permitAll();
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new VisualPasswordEncoder();
    }

}
