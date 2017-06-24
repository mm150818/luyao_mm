package top.toybus.luyao.api.formbean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActivityForm extends BaseForm {
    private int page = 0;

    private int size = 10;

    private Long id;

    private Long money;

    private Integer way;

}
