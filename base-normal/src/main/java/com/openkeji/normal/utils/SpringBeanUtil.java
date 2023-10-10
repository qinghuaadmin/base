package com.openkeji.normal.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringBeanUtil.applicationContext == null) {
            SpringBeanUtil.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name){
        if(applicationContext == null){
            return null;
        }
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        if(applicationContext == null){
            return null;
        }
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name,Class<T> clazz){
        if(applicationContext == null){
            return null;
        }
        return getApplicationContext().getBean(name, clazz);
    }
}
