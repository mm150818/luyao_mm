package top.toybus.luyao.api.formbean;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

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

    private String headImg;

    private String sign;

    private Integer sex;

    private String occupation;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private Long rideTemplateId;

    private String no;

    private String plateNo;

    private String model;

    private String travelImg;

    private String drivingImg;

    private String img;

}
