package top.toybus.luyao.api.formbean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MessageForm extends BaseForm {
    private int page; //页数

    private int size; //每页条数

    private Long id;

    private Long user_id;
    
    private String user_name;
    
    private Long mark_id;
     
    private String mark;

    private String message;
    
    //Sort sort =new Sort(Sort.Direction.DESC, "create_time");

}
