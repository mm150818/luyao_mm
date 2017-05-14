package top.toybus.luyao.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.api.formbean.UserForm;
import top.toybus.luyao.api.repository.RideRepository;
import top.toybus.luyao.api.repository.SmsRepository;
import top.toybus.luyao.api.repository.UserRepository;
import top.toybus.luyao.api.repository.UserRideRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.util.UUIDUtils;
import top.toybus.luyao.common.util.ValidatorUtils;

@Service
@Transactional
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SmsRepository smsRepository;
	@Autowired
	private RideRepository rideRepository;
	@Autowired
	private UserRideRepository userRideRepository;
	@Autowired
	private SmsHelper smsHelper;

	/**
	 * 注册用户
	 */
	public ResData registUser(UserForm userForm) {
		ResData resData = this.checkMobilePwd(userForm);
		if (!resData.isOk()) {
			return resData;
		}
		resData = this.checkVerifyCode(userForm);
		if (!resData.isOk()) {
			return resData;
		}
		if (resData.getCode() == 0) {
			// 获得最近发送的验证码
			Sms sms = smsRepository.findFirstByMobileAndStatusOrderByIdDesc(userForm.getMobile(), 0);
			resData = this.checkSmsVerifyCode(sms, userForm); // err1-3
			if (resData.getCode() == 0) {
				// 添加用户到数据库
				User user = userRepository.findUserByMobile(userForm.getMobile());
				if (user == null) {
					User newUser = new User();
					newUser.setMobile(userForm.getMobile());
					newUser.setPassword(DigestUtils.md5Hex(userForm.getPassword()));
					newUser.setBalance(new BigDecimal("0.00"));
					newUser.setOwner(false);
					newUser.setStatus(0);
					newUser.setCreateTime(LocalDateTime.now());
					newUser.setUpdateTime(LocalDateTime.now());
					user = userRepository.save(newUser);
					sms.setStatus(1); // 更新短信状态为已使用

					resData.put("user", user);
				} else {  // err4
					resData.setCode(4).setMsg("该手机号已经存在");
				}
			}
		}
		return resData;
	}

	/**
	 * 检查手机号，密码
	 */
	private ResData checkMobilePwd(UserForm userForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(userForm.getMobile())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入手机号");
		} else if (ValidatorUtils.isNotMobile(userForm.getMobile())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("手机号格式不正确");
		} else if (StringUtils.isBlank(userForm.getPassword())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入密码");
		} else if (ValidatorUtils.isNotPassword(userForm.getPassword())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("密码格式不正确");
		}
		return resData;
	}

	/**
	 * 检查验证码
	 */
	private ResData checkVerifyCode(UserForm userForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(userForm.getVerifyCode())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入验证码");
		} else if (ValidatorUtils.isNotVerifyCode(userForm.getVerifyCode())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("验证码格式不正确");
		}
		return resData;
	}

	/**
	 * 检查短信校验码
	 * err1-3
	 */
	private ResData checkSmsVerifyCode(Sms sms, UserForm userForm) {
		ResData resData = ResData.get();
		if (sms == null) {
			resData.setCode(1); // err1
			resData.setMsg("请发送短信验证码");
		} else if (!sms.getVerifyCode().equals(userForm.getVerifyCode())) {
			resData.setCode(2); // err2
			resData.setMsg("验证码不正确");
		} else if (LocalDateTime.now()
				.isAfter(sms.getCreateTime().plusMinutes(smsHelper.smsProperties.getValidMinutes()))) { // 10分钟超时
			sms.setStatus(2); // 更新短信状态为已过期

			resData.setCode(3).setMsg("验证码已过期");  // err3
		}
		return resData;
	}

	/**
	 * 用户登录
	 */
	public ResData loginUser(UserForm userForm) {
		ResData resData = this.checkMobilePwd(userForm);
		if (resData.isOk()) {
			User user = userRepository.findUserByMobile(userForm.getMobile());
			if (user == null) {
				resData.setCode(1).setMsg("该手机号未注册"); // err1
			} else if (!user.getPassword().equals(DigestUtils.md5Hex(userForm.getPassword()))) {
				resData.setCode(2).setMsg("密码输入错误"); // err2
			} else if (user.getStatus() == 1) { // er3
				resData.setCode(3).setMsg("用户已经登录");
				user.getIgnoreProps().remove("token"); // 序列化token

				resData.put("user", user);
			} else { // 更新用户状态为已登录
				user.setToken(UUIDUtils.randUUID()); // 产生新的令牌
				user.setStatus(1); // 已登录
				user.setUpdateTime(LocalDateTime.now());
				user.getIgnoreProps().remove("token"); // 序列化token

				resData.put("user", user);
			}
		}
		return resData;
	}

	/**
	 * 获得用户
	 */
	public ResData getUser(UserForm userForm) {
		ResData resData = ResData.get();
		User loginUser = userForm.getLoginUser();
		resData.put("user", loginUser);
		return resData;
	}

	/**
	 * 获得登录的用户
	 */
	public User getLoginUser(String token) {
		User user = userRepository.findUserByToken(token);
		return user;
	}

	/**
	 * 修改密码
	 */
	public ResData updateUserPwd(UserForm userForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(userForm.getOldPassword())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入原密码");
		} else if (ValidatorUtils.isNotPassword(userForm.getOldPassword())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("原密码格式不正确");
		} else if (StringUtils.isBlank(userForm.getPassword())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入新密码");
		} else if (ValidatorUtils.isNotPassword(userForm.getPassword())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("新密码格式不正确");
		}
		if (resData.isOk()) {
			User loginUser = userForm.getLoginUser();
			if (!loginUser.getPassword().equals(DigestUtils.md5Hex(userForm.getOldPassword()))) {
				resData.setCode(1).setMsg("原密码输入错误"); // err1
			} else {
				int count = userRepository.updateUserPwdById(loginUser.getId(),
						DigestUtils.md5Hex(userForm.getPassword()), LocalDateTime.now());
				if (count != 1) {
					resData.setCode(2).setMsg("密码修改失败"); // err2
				}
			}
		}
		return resData;
	}

	/**
	 * 找回密码
	 */
	public ResData findUserPwd(UserForm userForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(userForm.getMobile())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入手机号");
		} else if (ValidatorUtils.isNotMobile(userForm.getMobile())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("手机号格式不正确");
		} else if (this.checkVerifyCode(userForm).isOk()) {
			// 获得最近发送的验证码
			Sms sms = smsRepository.findFirstByMobileAndStatusOrderByIdDesc(userForm.getMobile(), 0);
			resData = this.checkSmsVerifyCode(sms, userForm); // err1-3
			if (resData.isOk()) {
				if (StringUtils.isBlank(userForm.getPassword())) {
					resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入新密码");
				} else if (ValidatorUtils.isNotPassword(userForm.getPassword())) {
					resData.setCode(ResData.C_PARAM_ERROR).setMsg("新密码格式不正确");
				}
				if (resData.isOk()) {
					User user = userRepository.findUserByMobile(userForm.getMobile());
					if (user == null) {
						resData.setCode(4).setMsg("该手机号未注册"); // err4
					} else {
						int count = userRepository.updateUserPwdById(user.getId(),
								DigestUtils.md5Hex(userForm.getPassword()), LocalDateTime.now());
						if (count != 1) {
							resData.setCode(5).setMsg("密码找回失败"); // err5
						}
						sms.setStatus(1); // 更新短信状态为已使用
					}
				}
			}
		}
		return resData;
	}

	/**
	 * 预约车次，修改预约车次
	 */
	public ResData orderRide(UserForm userForm) {
		ResData resData = ResData.get();
		if (userForm.getRideId() == null) {
			return resData.setCode(1).setMsg("请输入车次ID"); // err1
		}
		Ride ride = rideRepository.findOne(userForm.getRideId());
		if (ride == null) {
			return resData.setCode(2).setMsg("所选车次不存在"); // err2
		}

		User loginUser = userForm.getLoginUser();
		UserRide userRide = userRideRepository.findByUserIdAndRide(loginUser.getId(), ride);
		if (userRide == null) {
			if (ride.getRemainSeats() < userForm.getSeats()) {
				return resData.setCode(3).setMsg(String.format("所选车次目前只有%d个剩余座位", ride.getRemainSeats())); // err3
			}
			userRide = new UserRide();
			userRide.setUserId(loginUser.getId());
			userRide.setRide(ride);
			userRide.setSeats(userForm.getSeats());
			userRide.setCreateTime(LocalDateTime.now());
			userRide.setUpdateTime(LocalDateTime.now());

			userRide = userRideRepository.save(userRide); // 保存订单
			ride.setRemainSeats(ride.getSeats() - userRide.getSeats()); // 修改剩余座位数
			ride.setUpdateTime(LocalDateTime.now());

		} else { // 已经订购过则是修改订单
			if (ride.getRemainSeats() + userRide.getSeats() < userForm.getSeats()) {
				return resData.setCode(4)
						.setMsg(String.format("所选车次目前只有%d个剩余座位", ride.getRemainSeats() + userRide.getSeats())); // err4
			}
			int remainSeats = ride.getRemainSeats() + userRide.getSeats() - userForm.getSeats();

			userRide.setSeats(userForm.getSeats());
			ride.setRemainSeats(remainSeats); // 修改剩余座位数
			ride.setUpdateTime(LocalDateTime.now());
		}

		// 5月12日19点30分中潭路4号口不见不散（当用户预约并支付成功时收到的提醒）
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("name", String.format("[%s]", ride.getName()));
		paramMap.put("time", ride.getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
		paramMap.put("address", ride.getAddress());
		smsHelper.sendSms(loginUser.getMobile(), smsHelper.getSmsProperties().getTplOrderOk(), paramMap);

		resData.put("userRide", userRide);
		return resData;
	}

	/**
	 * 修改用户信息
	 */
	public ResData updateInfo(UserForm userForm) {
		ResData resData = ResData.get();
		User loginUser = userForm.getLoginUser();
		if (StringUtils.isNotBlank(userForm.getNickname())) {
			if (ValidatorUtils.isNotNickname(userForm.getNickname())) {
				return resData.setCode(ResData.C_PARAM_ERROR).setMsg("昵称格式不正确");
			}
			loginUser.setNickname(userForm.getNickname());
		}
		if (StringUtils.isNotBlank(userForm.getVehicleNo())) {
			if (ValidatorUtils.isNotVehicleNo(userForm.getVehicleNo())) {
				return resData.setCode(ResData.C_PARAM_ERROR).setMsg("车编号格式不正确");
			} else if (!loginUser.getOwner()) {
				return resData.setCode(1).setMsg("您不是车主，不能修改车编号"); // err1
			}
			loginUser.setVehicleNo(userForm.getVehicleNo());
		}
		if (userForm.getRideTemplateId() != null) {
			if (!loginUser.getOwner()) {
				return resData.setCode(2).setMsg("您不是车主，不能修改默认车次模板");  // err2
			} else if (rideRepository.existsByTemplateTrueAndId(userForm.getRideTemplateId())) {
				loginUser.setRideTemplateId(userForm.getRideTemplateId());
			} else {
				return resData.setCode(3).setMsg("车次模板不存在"); // err3
			}
		}
		loginUser.setUpdateTime(LocalDateTime.now());
		User user = userRepository.save(loginUser);

		resData.put("user", user);
		return resData;
	}

}
