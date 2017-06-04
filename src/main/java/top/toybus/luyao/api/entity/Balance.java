package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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

    @JsonInclude(Include.NON_NULL)
    private Integer way;

    private Long money;

    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "order_no")
    private Long orderNo;

    private Integer status;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> typeMap = new HashMap() {
        {
            put(1, "充值");
            put(2, "提现");
            put(3, "行程收入");
            put(4, "行程支出");
        }
    };

    public String getTypeStr() {
        return typeMap.get(this.type);
    }
}