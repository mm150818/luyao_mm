package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import top.toybus.luyao.common.properties.FileProperties;

/**
 * 用户(乘客、车主)
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

    private String mobile;

    @JsonInclude(Include.NON_NULL)
    private String token;

    @JsonIgnore
    @Column(updatable = false)
    private String password;

    @Column(name = "head_img")
    private String headImg;

    private String nickname;

    private String sign;

    private Integer sex;

    private String occupation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private Long balance;

    private Long income;

    @Column(name = "draw_cash")
    private Long drawCash;

    @Column(name = "ride_count")
    private Integer rideCount;

    private Integer status;

    private Boolean owner;

    @JsonInclude(Include.NON_NULL)
    @Transient
    private Vehicle vehicle;

    @JsonIgnore
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @JsonIgnore
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 默认忽略token属性的序列化，但是登录成功后需要
     * ####利用password字段决定token字段的序列化，默认password有值时不序列化token字段
     */
    public String getToken() {
        return password == null ? token : null;
    }

    public String getHeadImg() {
        return FileProperties.BASE_URL + this.headImg;
    }

    @JsonIgnore
    public boolean isNotOwner() {
        return this.owner == null || !this.owner;
    }

}
