package com.bdilab.aiflow.Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author smile
 * @data 2020/12/4 10:19
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/index.html")
                .excludePathPatterns("/login")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/swagger-ui.html","/swagger-resources/**","/images/**","/webjars/**","/v2/api-docs/**")
            .addPathPatterns("/**") ;
    }
}
