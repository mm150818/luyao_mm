package top.toybus.luyao.sys.formbean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserSysForm extends BaseSysForm {

    private String token;

    private Long id;

    private int page = 0;

    private int size = 10;

    private String nickname;

    private Integer owner;

    private Integer status;

}
