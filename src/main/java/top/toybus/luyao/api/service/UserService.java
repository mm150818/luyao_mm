package top.toybus.luyao.api.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.api.formbean.UserForm;
import top.toybus.luyao.api.repository.SmsRepository;
import top.toybus.luyao.api.repository.UserRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.util.UUIDUtils;

@Service
@Transactional
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SmsRepository smsRepository;

	@Value("${sms.expiration.time}")
	private int smsExpirationTime = 10 * 60 * 1000;

	/**
	 * 注册用户
	 */
	public ResData registUser(UserForm userForm) {
		ResData resData = this.verifyCodeForm(userForm);
		if (resData.getCode() == 0) {
			// 添加用户到数据库
			User user = userRepository.findUserByMobile(userForm.getMobile());
			if (user == null) {
				User newUser = new User();
				newUser.setMobile(userForm.getMobile());
				newUser.setToken(UUIDUtils.randUUID()); // 产生注册后令牌
				newUser.setBalance(new BigDecimal("0.00"));
				newUser.setOwner(false);
				newUser.setStatus(0);
				newUser.setCreateTime(new Date());
				newUser.setUpdateTime(new Date());
				user = userRepository.save(newUser);

				resData.put("user", user);
			} else {
				resData.setCode(ResData.C_USER_EXISTS);
				resData.setMsg("该手机号已经存在");
			}
		}
		return resData;
	}

	// 检查校验码
	private ResData verifyCodeForm(UserForm userForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(userForm.getMobile())) {
			resData.setCode(ResData.C_PARAM_ERROR);
			resData.setMsg("请输入手机号");
		} else if (StringUtils.isBlank(userForm.getCode())) {
			resData.setCode(ResData.C_PARAM_ERROR);
			resData.setMsg("请输入验证码");
		} else {
			// 获得最近发送的验证码
			Sms sms = smsRepository.findFirstByMobileAndStatusOrderByIdDesc(userForm.getMobile(), 0);
			if (sms == null) {
				resData.setCode(1);
				resData.setMsg("请发送短信验证码");
			} else if (!sms.getCode().equals(userForm.getCode())) {
				resData.setCode(2);
				resData.setMsg("验证码不正确");
			} else if (new Date().getTime() - sms.getCreateTime().getTime() > smsExpirationTime) { // 10分钟超时
				sms.setStatus(2); // 更新短信状态为已过期

				resData.setCode(3);
				resData.setMsg("验证码已过期");
			} else {
				sms.setStatus(1); // 更新短信状态为已使用
			}
		}
		return resData;
	}

	/**
	 * 用户登录
	 */
	public ResData loginUser(UserForm userForm) {
		ResData resData = this.verifyCodeForm(userForm);
		if (resData.getCode() == 0) {
			User user = userRepository.findUserByToken(userForm.getToken());
			if (user == null) {
				return resData.setCode(4).setMsg("登录令牌无效"); // 令牌无效
			} else if (user.getStatus() == 0) { // 更新用户状态为已登录
				user.setToken(UUIDUtils.randUUID()); // 产生新的令牌
				user.setStatus(1); // 已登录
				user.setUpdateTime(new Date());
			} else if (user.getStatus() == 1) {
				resData.setCode(5).setMsg("用户已经登录");
			}

			resData.put("user", user);
		}
		return resData;
	}

	/**
	 * 获得用户
	 */
	public ResData getUser(UserForm userForm) {
		ResData resData = ResData.get();
		User loginUser = userForm.getLoginUser();
		List<UserRide> userRideList = userRepository.findUserRideListByUser(loginUser);
		loginUser.setUserRideList(userRideList);
		resData.put("user", loginUser);
		return resData;
	}

	/**
	 * 获得登录的用户
	 */
	public User getLoginUser(String token) {
		User user = userRepository.findUserByToken(token);
		if (user != null) {
			user.setUserRideList(Collections.emptyList()); // 关闭级联userRideList查询
		}
		return user;
	}

}
