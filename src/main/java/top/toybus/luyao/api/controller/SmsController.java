package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.formbean.SmsForm;
import top.toybus.luyao.api.service.SmsService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 短信
 */
@RestController
@RequestMapping("/api/sms")
public class SmsController {
	@Autowired
	private SmsService smsService;

	/**
	 * 发送登录、注册短信验证码
	 */
	@RequestMapping("/sendCode")
	public ResData sendCode(SmsForm smsForm) {
		ResData resData = smsService.sendCode(smsForm);
		return resData;
	}
}
