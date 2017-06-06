package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Data;

/**
 * 用户-行程关联表
 */
@Data
@Entity
@Table(name = "tb_user_ride")
@SuppressWarnings("serial")
public class UserRide implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id")
    private Long userId;

//	@JsonIgnoreProperties({ "owner" })
    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private Integer seats;

    @ManyToOne
    @JoinColumn(name = "ride_via_id")
    private RideVia rideVia;

    @JsonUnwrapped
    @JsonIgnoreProperties({ "id", "tradeNo", "createTime", "notifyTime" })
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Boolean confirmed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonIgnore
    public Long getTotalAmount() {
        if (this.ride != null && this.seats != null) {
            return this.ride.getReward().longValue() * this.seats.intValue();
        }
        return null;
    }
}
