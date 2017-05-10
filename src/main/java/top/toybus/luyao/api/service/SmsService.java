package top.toybus.luyao.api.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.formbean.SmsForm;
import top.toybus.luyao.api.properties.SmsProperties;
import top.toybus.luyao.api.repository.SmsRepository;
import top.toybus.luyao.common.bean.ResData;

@Service
@Transactional
public class SmsService {
	@Autowired
	private SmsRepository smsRepository;

	@Autowired
	private SmsProperties smsProperties;

	/**
	 * 发送短信验证码
	 */
	public ResData sendCode(SmsForm smsForm) {
		ResData resData = new ResData();
		if (StringUtils.isBlank(smsForm.getMobile())) {
			resData.setCode(ResData.C_PARAM_ERROR);
			resData.setMsg("请输入手机号");
		} else {
			Sms newSms = new Sms();
			newSms.setMobile(smsForm.getMobile());
			// 发送短信
			String code = RandomStringUtils.randomNumeric(4);
			newSms.setCode(code);
			newSms.setContent(smsProperties.getCodeTemplate().replace("{code}", code).replace("{seconds}",
					String.valueOf(smsProperties.getValidSeconds() / 60)));
			newSms.setStatus(0);
			newSms.setCreateTime(LocalDateTime.now());
			newSms.setUpdateTime(LocalDateTime.now());

			smsRepository.save(newSms);
		}
		return resData;
	}
}
