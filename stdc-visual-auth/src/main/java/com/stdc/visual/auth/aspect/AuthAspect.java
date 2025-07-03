package com.stdc.visual.auth.aspect;

import com.stdc.core.auth.prop.AuthProperties;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

/**
 * 权限操作
 * AuthEnable 标注当前方法使用需要开启权限功能 否则不能访问
 */
@Slf4j
@Aspect
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AuthAspect {

	@Autowired
	private AuthProperties authProperties;

	//检验权限是否开启 注解
	@Around("@annotation(com.stdc.visual.auth.annotation.AuthEnable)")
	public Object authEnable(ProceedingJoinPoint point) throws Throwable {
		return authEnableApi(point);
	}
	/**
	 * AOP 环切 控制器 R 返回值
	 * @param point JoinPoint
	 * @return Object
	 * @throws Throwable 异常
	 */
	public Object authEnableApi(ProceedingJoinPoint point) throws Throwable {
		if (!authProperties.getEnable()){
			BaseException.throwException(ResultCode.get("auth_is_not_enable"));
		}
		return point.proceed();
	}



}
