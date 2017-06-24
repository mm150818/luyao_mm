package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 活动
 */
@Data
@Entity
@Table(name = "tb_activity")
@SuppressWarnings("serial")
public class Activity implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Long amount;

    private Integer total;

    private Integer discount;

    private Long extraAmount;

    private Boolean deleted;

    @JsonIgnore
    private LocalDateTime createTime;

    @JsonIgnore
    private LocalDateTime updateTime;
}
