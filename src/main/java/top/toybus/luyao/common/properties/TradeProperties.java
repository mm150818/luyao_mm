package top.toybus.luyao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("api.trade")
@Data
public class TradeProperties {
    // alipay
    private String ALI_URL;
    private String ALI_FORMAT = "json";
    private String ALI_CHARSET = "UTF-8";
    private String ALI_SIGN_TYPE = "RSA2";
    private String ALI_APP_ID;
    private String ALI_SELLER_ID;
    private String ALI_APP_PRIVATE_KEY;
    private String ALI_ALIPAY_PUBLIC_KEY;
    private String ALI_NOTIFY_URL;

    // wxpay
    private String WX_UNIFIEDORDER_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private String WX_ORDERQUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";
    private String WX_CLOSEORDER_API = "https://api.mch.weixin.qq.com/pay/closeorder";
    private String WX_REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    private String WX_REFUNDQUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";
    private String WX_DOWNLOADBILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";
    private String WX_NOTIFY_URL;
    private String WX_APPID;
    private String WX_KEY;
    private String WX_MCH_ID;
    private String WX_CERT_LOCAL_PATH;
    private String WX_CERT_PASSWORD;

    // 提现手续费百分比
    private int feePercent = 0;

}
