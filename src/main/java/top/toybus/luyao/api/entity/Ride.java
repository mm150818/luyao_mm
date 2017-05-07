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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 顺风车
 */
@Data
//@ToString(exclude = { "rideUserList", "user" })
@JsonIgnoreProperties({ "user.token" })
@Entity
@Table(name = "tb_ride")
@SuppressWarnings("serial")
public class Ride implements Serializable {
	@Id
	@GeneratedValue
	private Long id;

	@JsonIgnoreProperties("userRideList")
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String name;

	@Column(name = "img_url")
	private String imgUrl;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date time;

	private BigDecimal reward;

	private Integer seats;

	@Column(name = "remain_seats")
	private Integer remainSeats;

	@Column(name = "start_point")
	private String startPoint;

	@Column(name = "end_point")
	private String endPoint;

	private String via1;

	private String via2;

	@JsonIgnore
	@Column(name = "create_time")
	private Date createTime;

	@JsonIgnore
	@Column(name = "update_time")
	private Date updateTime;

	@OneToMany
	@JoinColumn(name = "ride_id")
	private List<RideUser> rideUserList = new ArrayList<>();

	public String getReward() {
		return reward == null ? null : reward.toString();
	}
}
