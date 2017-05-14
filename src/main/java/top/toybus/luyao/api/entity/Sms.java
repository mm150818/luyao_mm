package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
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

	@Column(name = "verify_code")
	private String verifyCode;

	private Integer status;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "update_time")
	private LocalDateTime updateTime;
}
