package top.toybus.luyao.api.formbean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedPacketForm extends BaseForm {
    
	private int judge; //判断标志  1：发布消息；2：分享消息；null：其他无奖励状态

    private Long user_id;
    
    private String user_name;
    
    private String return_message; //返回信息
    
}
