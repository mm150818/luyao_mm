package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.Data;

/**
 * 短信
 */
@Data
@Entity
@Table(name = "tb_payment")
@SuppressWarnings("serial")
public class Payment implements Serializable {
    @Id
    @PrimaryKeyJoinColumn(name = "user_ride_id")
    private Long userRideId;

    private Integer way;

    @Column(name = "out_trade_no")
    private Long outTradeNo;

    @Column(name = "trade_no")
    private String tradeNo;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "notify_time")
    private LocalDateTime notifyTime;
}
