package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 短信
 */
@Data
@Entity
@Table(name = "tb_sms")
@SuppressWarnings("serial")
public class Sms implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String mobile;

    private String verifyCode;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
