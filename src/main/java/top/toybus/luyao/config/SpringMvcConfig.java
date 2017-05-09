package top.toybus.luyao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
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

	@Bean
	public HandlerInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 【需要登录】拦截器
		registry.addInterceptor(loginInterceptor());

		super.addInterceptors(registry);
	}

}
