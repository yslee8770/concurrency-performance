package com.example.jpa_concurrency_performance_lab.config;

import javax.sql.DataSource;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
