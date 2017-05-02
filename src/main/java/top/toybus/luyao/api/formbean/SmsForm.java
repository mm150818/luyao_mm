package top.toybus.luyao.api.formbean;

import lombok.Data;
import top.toybus.luyao.api.entity.Sms;

@Data
public class SmsForm {
    private Sms sms = new Sms();
}
