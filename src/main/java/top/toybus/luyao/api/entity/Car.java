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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 顺风车
 */
@Data
@Entity
@Table(name = "tb_car")
@SuppressWarnings("serial")
public class Car implements Serializable {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@Column(name = "headimg_url")
	private String headimgUrl;

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
	@JoinColumn(name = "car_id")
	private List<CarUser> carUserList = new ArrayList<>();

	public String getReward() {
		return reward == null ? null : reward.toString();
	}
}
