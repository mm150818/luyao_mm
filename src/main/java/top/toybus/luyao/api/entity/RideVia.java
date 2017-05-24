package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 行程途经
 */
@Data
@Entity
@Table(name = "tb_ride_via")
@SuppressWarnings("serial")
public class RideVia implements Serializable {
	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "ride_id")
	private Long rideId;

	private String point;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime time;

}
