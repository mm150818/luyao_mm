package top.toybus.luyao.common.helper;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.SmsAttributes;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.common.properties.SmsProperties;

/**
 * 短信助手
 * 
 * @author sunxg
 */
@Log4j
@Component
public class SmsHelper {
    @Autowired
    private Environment environment;
    @Getter
    @Autowired
    public SmsProperties smsProperties;

    /**
     * 不是正式环境
     */
    public boolean isNotProdEnv() {
        return environment.acceptsProfiles("!prod");
    }

    /**
     * 发送订购成功短信
     */
    public void sendOrderOkSms(User user, UserRide userRide) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", user.getNickname());
        paramMap.put("code", userRide.getPayment().getOrderNo().toString());
        paramMap.put("time", userRide.getRide().getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
        paramMap.put("address", userRide.getRideVia().getPoint());
        paramMap.put("plane", userRide.getRide().getOwner().getVehicle().getPlateNo());
        paramMap.put("tel", userRide.getRide().getOwner().getMobile());
        // 给乘客发短信
        this.sendSms(user.getMobile(), this.getSmsProperties().getTplOrderOkForUser(), paramMap);
        paramMap.put("name", userRide.getRide().getOwner().getNickname());
        paramMap.put("username", user.getNickname());
        paramMap.put("time", userRide.getRide().getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
        paramMap.put("tel", user.getMobile());
        // 给司机发短信
        this.sendSms(userRide.getRide().getOwner().getMobile(), this.getSmsProperties().getTplOrderOkForOwner(),
                paramMap);
    }

    /**
     * @param mobile
     *            手机号
     * @param tplCode
     *            短信模板代码
     * @param paramMap
     *            短信模板参数
     * 
     * @return 是否发送成功
     */
    public boolean sendSms(String mobile, String tplCode, Map<String, String> paramMap) {
        if (log.isInfoEnabled()) {
            log.info(String.format("发送短信至手机号：%s，模板代码：%s，模板参数：%s", mobile, tplCode, paramMap));
        }
        // 非正式环境不发真正短信
        if (this.isNotProdEnv()) {
            return true;
        }
        /**
         * Step 1. 获取主题引用
         */
        CloudAccount cloudAccount = new CloudAccount(smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret(),
                smsProperties.getEndpoint());
        MNSClient mnsClient = cloudAccount.getMNSClient();
        CloudTopic cloudTopic = mnsClient.getTopicRef(smsProperties.getTopic());
        /**
         * Step 2. 设置SMS消息体（必须）
         *
         * 注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
         */
        RawTopicMessage rawTopicMessage = new RawTopicMessage();
        rawTopicMessage.setMessageBody("sms-msg");

        /**
         * Step 3. 生成SMS消息属性
         */
        SmsAttributes smsAttributes = new SmsAttributes();
        // 3.1 设置发送短信的签名（SMSSignName）
        smsAttributes.setFreeSignName(smsProperties.getSignName());
        // 3.2 设置发送短信使用的模板（SMSTempateCode）
        smsAttributes.setTemplateCode(tplCode);
        // 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
        if (MapUtils.isNotEmpty(paramMap)) {
            paramMap.forEach((key, value) -> smsAttributes.setSmsParam(key, value));
        }
        // 3.4 设置接收短信的号码
        smsAttributes.setReceiver(mobile);

        MessageAttributes messageAttributes = new MessageAttributes();
        messageAttributes.setSmsAttributes(smsAttributes);

        try {
            /**
             * Step 4. 发布SMS消息
             */
            cloudTopic.publishMessage(rawTopicMessage, messageAttributes);
        } catch (ServiceException se) {
            log.error(se.getMessage(), se);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            mnsClient.close();
        }
        return true;
    }

    /**
     * @param mobile
     *            手机号
     * @param tplCode
     *            短信模板代码
     * 
     * @return 是否发送成功
     */
    public boolean sendSms(String mobile, String tplCode) {
        return sendSms(mobile, tplCode, null);
    }

    /**
     * 发送短信验证码
     * 
     * @param mobile
     * @param verifyCode
     * @return
     */
    public boolean sendSmsVerifyCode(String mobile, String verifyCode) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("code", verifyCode);
        paramMap.put("minutes", String.valueOf(smsProperties.getValidMinutes()));
        return sendSms(mobile, smsProperties.getTplVerifyCode(), paramMap);
    }

}
