package com.example.multitenant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourceConfig {
    @Bean
    public TenantAwareDataSource dataSource(
            TenantRequestContext tenantRequestContext,
            @Value("${db.user}") String user,
            @Value("${db.password}") String password,
            @Value("${db.url}") String url
    ) {
        return new TenantAwareDataSource(
                tenantRequestContext,
                DataSourceBuilder.create()
                        .username(user)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .url(url)
                        .build()
        );
    }
}

