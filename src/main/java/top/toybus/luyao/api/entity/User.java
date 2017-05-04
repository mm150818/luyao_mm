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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 用户
 */
@Data
@Entity
@Table(name = "tb_user")
@SuppressWarnings("serial")
public class User implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;
    @JsonIgnore
    private String token;
    private String mobile;
    private String nickname;
    private BigDecimal balance;
    private Integer status;
    @JsonIgnore
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;
    @JsonIgnore
    @Column(name = "update_time")
    private Date updateTime;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "tb_user_car", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "car_id"))
    private List<Car> carList = new ArrayList<>();

    @Transient
    public String getBalance() {
	return balance == null ? null : balance.toString();
    }
}
