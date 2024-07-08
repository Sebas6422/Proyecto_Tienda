package com.example.Almacen;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public FilterRegistrationBean<ContentSecurityPolicyFilter> contentSecurityPolicyFilter() {
        FilterRegistrationBean<ContentSecurityPolicyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentSecurityPolicyFilter());
        registrationBean.addUrlPatterns("/*"); // Aplica el filtro a todas las URL
        return registrationBean;
    }
}
