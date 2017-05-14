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

	private String oldPassword;

	private String password;

	private String verifyCode;

	private Long rideId;

	private int seats;

	private String nickname;

	private String vehicleNo;

	private Long rideTemplateId;

}
