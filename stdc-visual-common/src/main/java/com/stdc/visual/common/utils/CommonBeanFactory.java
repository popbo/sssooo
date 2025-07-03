package com.stdc.visual.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: wang_jie
 * @data: 2022/5/17--16:29
 * @describe:
 */
@Component
public class CommonBeanFactory implements ApplicationContextAware {

    @Autowired
    private ApplicationContext applicationContext;

    private static ApplicationContext context;

    public CommonBeanFactory() {
    }

    @PostConstruct
    private void setContext(){
        CommonBeanFactory.context = applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        CommonBeanFactory.context = ctx;
    }

    public static Object getBean(String beanName) {
        try {
            return context != null && !StringUtils.isBlank(beanName) ? context.getBean(beanName) : null;
        } catch (BeansException e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }
}