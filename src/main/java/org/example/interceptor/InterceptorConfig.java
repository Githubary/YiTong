package org.example.interceptor;

/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 22:16
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并指定拦截的路径
        registry.addInterceptor(new UserInterceptor()).addPathPatterns("/admin/**");
    }
}

