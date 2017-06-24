package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 支付
 */
@Data
@Entity
@Table(name = "tb_payment")
@SuppressWarnings("serial")
public class Payment implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private Integer type;

    private Integer way;

    private Long orderNo;

    @JsonIgnore
    private String tradeNo;

    private Long totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonIgnore
    private LocalDateTime notifyTime;

    private Integer status;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> statusMap = new HashMap() {
        {
            put(0, "未支付(已创建)");
            put(1, "已支付");
            put(2, "(用户)已取消");
            put(3, "已关闭/支付失败");
        }
    };

    public String getStatusStr() {
        return statusMap.get(this.status);
    }

    public String getWayStr() {
        if (way == null) {
            return "";
        }
        return way == 1 ? "支付宝" : way == 2 ? "微信" : null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> typeMap = new HashMap() {
        {
            put(1, "行程");
            put(2, "充值");
            put(3, "提现");
            put(4, "绑定账户");
        }
    };

    public String getTypeStr() {
        return typeMap.get(this.type);
    }
}
