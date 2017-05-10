package top.toybus.luyao.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("api.sms")
@Data
public class SmsProperties {
	// 短信有效时间，默认10分钟
	private int validSeconds = 10 * 60;
	// 验证码模板
	private String codeTemplate = "【路遥】验证码：{code}。{seconds}分钟内有效。";
}
