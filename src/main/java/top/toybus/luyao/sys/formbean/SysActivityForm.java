package top.toybus.luyao.sys.formbean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SysActivityForm extends SysBaseForm {

    private int page = 0;

    private int size = 10;

    private Long id;

    private String name;

    private Long amount;

    private Integer total;

    private Integer discount;

    private Long extraAmount;

    private Boolean deleted;

}
