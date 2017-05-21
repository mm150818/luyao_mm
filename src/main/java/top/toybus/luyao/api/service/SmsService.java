package top.toybus.luyao.api.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.formbean.SmsForm;
import top.toybus.luyao.api.repository.SmsRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.util.ValidatorUtils;

@Service
@Transactional
public class SmsService {
    @Autowired
    private SmsRepository smsRepository;

    @Autowired
    private SmsHelper smsHelper;

    /**
     * 发送短信验证码
     */
    public ResData sendVerifyCode(SmsForm smsForm) {
        ResData resData = new ResData();
        if (StringUtils.isBlank(smsForm.getMobile())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入手机号");
        } else if (ValidatorUtils.isNotMobile(smsForm.getMobile())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("手机号格式不正确");
        } else {
            String verifyCode = RandomStringUtils.randomNumeric(4); // 短信验证码
            boolean isOk = smsHelper.sendSmsVerifyCode(smsForm.getMobile(), verifyCode); // 发送短信验证码
            if (isOk) {
                Sms newSms = new Sms();
                newSms.setMobile(smsForm.getMobile());
                newSms.setVerifyCode(verifyCode);
                newSms.setStatus(0);
                newSms.setCreateTime(LocalDateTime.now());
                newSms.setUpdateTime(LocalDateTime.now());

                smsRepository.save(newSms);
            } else {
                resData.setCode(1).setMsg("验证码短信发送失败，请稍后再试"); // err1
            }
        }
        return resData;
    }
}
