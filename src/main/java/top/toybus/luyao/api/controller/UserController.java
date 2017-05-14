package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.formbean.UserForm;
import top.toybus.luyao.api.service.UserService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 用户
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private UserService userService;

	/**
	 * 用户注册
	 */
	@RequestMapping("/regist")
	public ResData regist(UserForm userForm) {
		ResData resData = userService.registUser(userForm);
		return resData;
	}

	/**
	 * 用户登录
	 */
	@RequestMapping("/login")
	public ResData login(UserForm userForm) {
		ResData resData = userService.loginUser(userForm);
		return resData;
	}

	/**
	 * 用户信息
	 */
	@LoginRequired
	@RequestMapping("/info")
	public ResData info(UserForm userForm) {
		ResData resData = userService.getUser(userForm);
		return resData;
	}

	/**
	 * 用户信息修改
	 */
	@LoginRequired
	@RequestMapping("/updateInfo")
	public ResData updateInfo(UserForm userForm) {
		ResData resData = userService.updateInfo(userForm);
		return resData;
	}

	/**
	 * 修改密码
	 */
	@LoginRequired
	@RequestMapping("/updatePwd")
	public ResData updatePwd(UserForm userForm) {
		ResData resData = userService.updateUserPwd(userForm);
		return resData;
	}

	/**
	 * 找回密码
	 */
	@RequestMapping("/findPwd")
	public ResData findPwd(UserForm userForm) {
		ResData resData = userService.findUserPwd(userForm);
		return resData;
	}

	/**
	 * 用户订购车次
	 */
	@LoginRequired
	@RequestMapping("/orderRide")
	public ResData orderRide(UserForm userForm) {
		ResData resData = userService.orderRide(userForm);
		return resData;
	}

}
