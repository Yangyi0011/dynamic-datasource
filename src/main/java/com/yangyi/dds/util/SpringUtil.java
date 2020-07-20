package com.yangyi.dds.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

public class SpringUtil implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringUtil.class);
    private static ApplicationContext context;

    public SpringUtil() {
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        setStaticApplicationContext(context);
    }

    public static void setStaticApplicationContext(ApplicationContext context) {
        context = context;
    }

    public static <T> T getBean(Class<T> clazz) {
        try {
            return context.getBean(clazz);
        } catch (Exception var2) {
            log.error("can not fetch the assign bean by class!", var2);
            return null;
        }
    }

    public static <T> T getBean(String beanId) {
        try {
            return (T) context.getBean(beanId);
        } catch (Exception var2) {
            log.error("can not fetch the assign bean by beanId!", var2);
            return null;
        }
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        if (clazz != null && null != beanName && !"".equals(beanName.trim())) {
            try {
                return context.getBean(beanName, clazz);
            } catch (Exception var3) {
                log.error("can not fetch the assign bean by beanId!", var3);
                return null;
            }
        } else {
            return null;
        }
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void publishEvent(ApplicationEvent event) {
        if (context != null) {
            try {
                context.publishEvent(event);
            } catch (Exception var2) {
                log.error(var2.getMessage());
            }

        }
    }
}