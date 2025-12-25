package com.rejs.reservation.global.trace.config;

import com.rejs.reservation.global.trace.filter.ServletLoggingFilter;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ObservationConfig {
    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    @Bean public FilterRegistrationBean<ServletLoggingFilter> loggingFilterFilterRegistrationBean(){
        FilterRegistrationBean<ServletLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(servletLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        // traceId를 생성해주는 ServerHttpObservationFilter보다 늦게 실행되어야한다
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+30);
        return registrationBean;
    }

    @Bean
    public ServletLoggingFilter servletLoggingFilter(){
        return new ServletLoggingFilter();
    }
}
