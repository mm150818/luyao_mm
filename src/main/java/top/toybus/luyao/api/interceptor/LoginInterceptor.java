package top.toybus.luyao.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.exception.NotLoginException;
import top.toybus.luyao.api.service.UserService;

/**
 * 登录拦截器
 * 
 * @author sunxg
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		LoginRequired loginRequiredAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
		// 标注LoginRequired注解的值为true，需要在登录情况下操作
		if (loginRequiredAnnotation != null && loginRequiredAnnotation.value()) {
			String token = request.getParameter("token");
			User loginUser = null;
			if (StringUtils.isNotBlank(token) && StringUtils.length(token) == 32) {
				loginUser = userService.getLoginUser(token);
			} else {
				throw new NotLoginException("请输入正确的令牌");
			}
			if (loginUser == null) {
				throw new NotLoginException("登录令牌无效");
			} else if (loginUser.getStatus() != 1) {
				throw new NotLoginException("请先登录");
			}
			request.setAttribute(LoginRequired.LOGIN_USER, loginUser);
		}
		return super.preHandle(request, response, handler);
	}

}
