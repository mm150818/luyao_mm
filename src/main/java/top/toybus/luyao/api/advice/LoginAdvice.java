package top.toybus.luyao.api.advice;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.exception.NotLoginException;
import top.toybus.luyao.api.formbean.BaseForm;
import top.toybus.luyao.common.bean.ResData;

/**
 * 登录的增强处理
 */
@RestControllerAdvice(basePackages = "top.toybus.luyao.api.controller", annotations = RestController.class)
public class LoginAdvice {

	@InitBinder
	public void initBinder(WebDataBinder binder,
			@RequestAttribute(name = LoginRequired.LOGIN_USER, required = false) User loginUser) {
		// 将登录用户的信息放在formbean中
		if (binder.getTarget() instanceof BaseForm) {
			BaseForm baseForm = (BaseForm) binder.getTarget();
			baseForm.setLoginUser(loginUser);
		}
	}

	/*@ModelAttribute(LoginRequired.LOGIN_USER)
	public User getLoginUser(@RequestAttribute(name = LoginRequired.LOGIN_USER, required = false) User loginUser) {
		System.out.println(loginUser);
		return new User();
	}*/

	/**
	 * 未登录异常处理
	 */
	@ExceptionHandler(NotLoginException.class)
	public ResData handlerNotLoginException(NotLoginException e) {
		return new ResData(ResData.SC_NOT_LOGIN, e.getMessage());
	}
}
