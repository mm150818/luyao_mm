package top.toybus.luyao.api.formbean;

import lombok.Data;
import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.entity.User;

@Data
public class UserForm {
    private User user = new User();
    private Sms sms = new Sms();
}
