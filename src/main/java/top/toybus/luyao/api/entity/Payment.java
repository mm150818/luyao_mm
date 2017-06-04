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
 * 支付
 */
@Data
@Entity
@Table(name = "tb_payment")
@SuppressWarnings("serial")
public class Payment implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private Integer type;

    @Column(name = "target_id")
    private Long targetId;

    private Integer way;

    @Column(name = "out_trade_no")
    private Long outTradeNo;

    @Column(name = "trade_no")
    private String tradeNo;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "notify_time")
    private LocalDateTime notifyTime;
}
