package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

/**
 * 用户(乘客、车主)
 */
@Data
@ToString(exclude = { "userRideList" })
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "tb_user")
@SuppressWarnings("serial")
public class User implements Serializable {
	@JsonIgnore
	@Id
	@GeneratedValue
	private Long id;

	private String mobile;

	private String token;

	private String nickname;

	private BigDecimal balance;

	private Boolean owner;

	@Column(name = "ride_no")
	private String rideNo;

	@Column(name = "ride_template_id")
	private Long rideTemplateId;

	private Integer status;

	@JsonIgnore
	@Column(name = "create_time")
	private Date createTime;

	@JsonIgnore
	@Column(name = "update_time")
	private Date updateTime;

	@OneToMany
	@JoinColumn(name = "user_id", updatable = false)
	private List<UserRide> userRideList = new ArrayList<>();

	public String getBalance() {
		return balance == null ? null : balance.toString();
	}
}
