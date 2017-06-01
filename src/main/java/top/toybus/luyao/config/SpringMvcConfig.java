package top.toybus.luyao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import top.toybus.luyao.api.interceptor.LoginInterceptor;

/**
 * spring mvc 配置
 * 
 * @author sunxg
 */
@Configuration
public class SpringMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 自定义跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**"); // 所有api允许跨域访问

        super.addCorsMappings(registry);
    }

    // 登录拦截器
    @Bean
    public HandlerInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    /**
     * 自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // API【需要登录】拦截器
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/api/**");

        super.addInterceptors(registry);
    }

}
