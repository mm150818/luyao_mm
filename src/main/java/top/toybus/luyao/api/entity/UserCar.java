package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 用户-顺风车关联表
 */
@Data
@Entity
@Table(name = "tb_user_car")
@SuppressWarnings("serial")
public class UserCar implements Serializable {
	@Id
	@GeneratedValue
	private Long id;

	// @JsonUnwrapped
	@Column(name = "user_id")
	private Long userId;

	@JsonIgnoreProperties("carUserList")
	@ManyToOne
	@JoinColumn(name = "car_id")
	private Car car;

	private Integer seats;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time")
	private Date updateTime;
}
