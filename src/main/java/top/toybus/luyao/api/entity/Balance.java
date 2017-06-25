package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 账户交易
 */
@Data
@Entity
@Table(name = "tb_balance")
@SuppressWarnings("serial")
public class Balance implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @JsonIgnore
    private Long paymentId;

    private Integer way;

    private Long money;

    public Long getMoney() {
        if (this.type == 2 || this.type == 4) {
            return -this.money;
        }
        return this.money;
    }

    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> typeMap = new HashMap() {
        {
            put(1, "充值");
            put(2, "提现");
            put(3, "行程收入");
            put(4, "行程支出");
            put(5, "绑定账户");
        }
    };

    public String getTypeStr() {
        return typeMap.get(this.type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> wayMap = new HashMap() {
        {
            put(1, "支付宝");
            put(2, "微信");
            put(3, "余额支付");
        }
    };

    public String getWayStr() {
        return wayMap.get(this.way);
    }
}
