package com.examsystem.onlineexam.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

@Component
public class DataSourcePropertiesCustomizer implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties properties) {
            String url = properties.getUrl();
            if (url != null && !url.isBlank()) {
                if (url.startsWith("mysql://")) {
                    url = "jdbc:" + url;
                }
                if (url.startsWith("jdbc:mysql:")) {
                    properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    if (!url.contains("allowPublicKeyRetrieval")) {
                        url += (url.contains("?") ? "&" : "?") + "allowPublicKeyRetrieval=true&useSSL=false";
                    }
                } else if (url.startsWith("jdbc:h2:")) {
                    properties.setDriverClassName("org.h2.Driver");
                }
                properties.setUrl(url);
            }
        }
        return bean;
    }
}