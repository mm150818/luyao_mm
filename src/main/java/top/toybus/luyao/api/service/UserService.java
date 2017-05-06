package top.toybus.luyao.api.service;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.entity.User;
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
		ResData resData = this.verifyCode(userForm);
		if (resData.getSc() == 0) {
			// 添加用户到数据库

			User user = userRepository.findFirstByMobile(userForm.getMobile());
			if (user == null) {
				User newUser = new User();
				newUser.setMobile(userForm.getMobile());
				newUser.setToken(UUIDUtils.randUUID());
				newUser.setBalance(new BigDecimal("0.00"));
				newUser.setStatus(0);
				newUser.setCreateTime(new Date());
				newUser.setUpdateTime(new Date());
				user = userRepository.save(newUser);

				resData.put("user", user);
			} else {
				resData.setSc(ResData.SC_USER_EXISTS);
				resData.setMsg("该手机号已经存在");
			}
		}
		return resData;
	}

	// 检查校验码
	private ResData verifyCode(UserForm userForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(userForm.getMobile())) {
			resData.setSc(ResData.SC_PARAM_ERROR);
			resData.setMsg("请输入手机号");
		} else if (StringUtils.isBlank(userForm.getCode())) {
			resData.setSc(ResData.SC_PARAM_ERROR);
			resData.setMsg("请输入验证码");
		} else {
			// 获得最近发送的验证码
			Sms sms = smsRepository.findFirstByMobileAndStatusOrderByIdDesc(userForm.getMobile(), 0);
			if (sms == null) {
				resData.setSc(1);
				resData.setMsg("请发送短信验证码");
			} else if (!sms.getCode().equals(userForm.getCode())) {
				resData.setSc(2);
				resData.setMsg("验证码不正确");
			} else if (new Date().getTime() - sms.getCreateTime().getTime() > smsExpirationTime) { // 10分钟超时
				sms.setStatus(2); // 更新短信状态为已过期
				resData.setSc(3);
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
		ResData resData = this.verifyCode(userForm);
		if (resData.getSc() == 0) {
			// 更新用户状态为已登录
			User user = userRepository.findFirstByMobile(userForm.getMobile());
			if (user.getStatus() == 0) {
				user.setStatus(1); // 已登录
				user.setUpdateTime(new Date());
				user.setToken(UUIDUtils.randUUID()); // 更新token
			} else if (user.getStatus() == 1) {
				resData.setSc(1);
				resData.setMsg("用户已经登录");
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
		User user = null;
		if (StringUtils.isNotBlank(userForm.getMobile())) {
			user = userRepository.findFirstByMobile(userForm.getMobile());
		}
		resData.put("user", user);
		return resData;
	}

}
