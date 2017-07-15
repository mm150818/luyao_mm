package top.toybus.luyao.common.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件助手
 * 
 * @author sunxg
 */
//@Log4j
@Component
public class MailHelper {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${api.mail.to}")
    private String sender;
    @Autowired
    private Environment environment;

    /**
     * 不是正式环境
     */
    public boolean isNotProdEnv() {
        return environment.acceptsProfiles("!prod");
    }

    /**
     * 管理员给自己发邮件
     */
    public void sendSimpleMail(String subject, String text) {
//        if (isNotProdEnv()) {
//            return;
//        }
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom(sender);
        simpleMessage.setTo(sender);
        simpleMessage.setSubject(subject);
        simpleMessage.setText(text);
        javaMailSender.send(simpleMessage);
    }
}
