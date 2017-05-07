package top.toybus.luyao.api.formbean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserForm extends BaseForm {
	private String token;

	private String mobile;

	private String code;

}
