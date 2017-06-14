package top.toybus.luyao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("api.sms")
@Data
public class SmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String topic;
    private String signName;

    private int validMinutes = 10; // 短信有效时间，默认10分钟

    // 短信模板CODE
    private String tplVerifyCode; // 1.验证码（注册，改密码）
    private String tplRidePubOk; // 2.您的信息已发布成功（当用户发布班次信息成功时收到的提醒）
    private String tplOrderOk; // 3.5月12日19点30分中潭路4号口不见不散（当用户预约并支付成功时收到的提醒）
    private String tplRideDelOk; // 4.您发布的信息已成功删除（当用户删除尚未有人预约的班次时收到的提醒）
    private String tplRideUpdOk; // 5.您成功调整了发布的信息（当用户修改尚未有人预约的班次时收到的提醒）
    private String tplDepositOk; // 6.您成功提现200元到支付宝、微信账户（用户提现成功的时候）
    private String tplEncashOk; // 7.充值成功，您的账户余额200元（当用户自己选择冲值的时候）
    private String tplOwnerOk; // 8.您已经通过车主认证，赶紧去发布信息吧
    private String tplOwnerFail; // 8.您已经通过车主认证，赶紧去发布信息吧

}
