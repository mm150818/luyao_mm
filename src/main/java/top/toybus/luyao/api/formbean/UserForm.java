package top.toybus.luyao.api.formbean;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private int page = 0;

    private int size = 10;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Long money;

    private Integer way;

    private String account;

    private Integer[] owner;
}
