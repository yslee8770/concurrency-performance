package com.example.config;

import javax.sql.DataSource;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceProxyConfig {

    @Bean
    public static BeanPostProcessor dataSourceProxyPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof DataSource ds && "dataSource".equals(beanName)) {
                    return ProxyDataSourceBuilder
                            .create(ds)
                            .name("DS")
                            .countQuery()
                            .build();
                }
                return bean;
            }
        };
    }
}
