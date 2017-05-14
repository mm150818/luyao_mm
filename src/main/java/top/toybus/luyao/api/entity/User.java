package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 用户(乘客、车主)
 */
@Data
@Entity
@Table(name = "tb_user")
@SuppressWarnings("serial")
public class User implements Serializable {
	@JsonIgnore
	@Transient
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<String> ignoreProps = new HashSet() {
		{
			add("token"); // 默认忽略token字段序列化
		}
	};

	@JsonIgnore
	@Id
	@GeneratedValue
	private Long id;

	private String mobile;

	@JsonIgnore
	@Column(updatable = false)
	private String password;

	@JsonInclude(Include.NON_NULL)
	private String token;

	private String nickname;

	private BigDecimal balance;

	private Boolean owner;

	@Column(name = "vehicle_no")
	private String vehicleNo;

	@Column(name = "ride_template_id")
	private Long rideTemplateId;

	private Integer status;

	@JsonIgnore
	@Column(name = "create_time")
	private LocalDateTime createTime;

	@JsonIgnore
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	// 默认忽略token属性的序列化，但是登录成功后需要
	public String getToken() {
		return ignoreProps.contains("token") ? null : token;
	}

	public String getBalance() {
		return balance == null ? null : balance.toString();
	}

}
