package top.toybus.luyao.api.service;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.formbean.SmsForm;
import top.toybus.luyao.api.repository.SmsRepository;
import top.toybus.luyao.common.bean.ResData;

@Service
@Transactional
public class SmsService {
	@Autowired
	private SmsRepository smsRepository;

	@Value("${sms.expiration.time}")
	private int smsExpirationTime = 10 * 60 * 1000;
	@Value("${sms.template.verifyCode}")
	private String smsTemplateVerifyCode = "【路遥】验证码：{code}。10分钟内有效。";

	/**
	 * 发送短信验证码
	 */
	public ResData sendCode(SmsForm smsForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(smsForm.getMobile())) {
			resData.setSc(ResData.SC_PARAM_ERROR);
			resData.setMsg("请输入手机号");
		} else {
			Sms newSms = new Sms();
			newSms.setMobile(smsForm.getMobile());
			// 发送短信
			String code = RandomStringUtils.randomNumeric(4);
			newSms.setCode(code);
			newSms.setContent(smsTemplateVerifyCode.replace("{code}", code));
			newSms.setStatus(0);
			newSms.setCreateTime(new Date());
			newSms.setUpdateTime(new Date());

			smsRepository.save(newSms);
		}
		return resData;
	}
}
