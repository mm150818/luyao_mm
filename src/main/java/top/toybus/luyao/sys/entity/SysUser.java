package top.toybus.luyao.sys.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import top.toybus.luyao.common.properties.FileProperties;

/**
 * 用户(乘客、车主)
 */
@Data
@Entity
@Table(name = "tb_user")
@SuppressWarnings("serial")
public class SysUser implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    private String mobile;

    private String token;

    @JsonIgnore
    @Column(updatable = false)
    private String password;

    private String headImg;

    private String nickname;

    private String sign;

    private Integer sex;

    private String occupation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private Long balance;

    private Long income;

    private Long drawCash;

    private Integer rideCount;

    private Integer status;

    private Integer owner;

    private Long vehicleId;

    @Transient
    private SysVehicle vehicle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public String getHeadImg() {
        if (this.headImg == null) {
            return null;
        }
        return FileProperties.BASE_URL + this.headImg;
    }

    @JsonIgnore
    public boolean isNotOwner() {
        return this.owner != 1;
    }

    public String getSexStr() {
        return this.sex == null ? "" : this.sex == 1 ? "男" : this.sex == 0 ? "女" : "未知(保密)";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> ownerMap = new HashMap() {
        {
            put(0, "不是车主");
            put(1, "是车主");
            put(2, "车主审核中");
            put(3, "车主审核失败");
        }
    };

    public String getOwnerStr() {
        return ownerMap.get(this.owner);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> statusMap = new HashMap() {
        {
            put(0, "未登录");
            put(1, "已登录");
            put(-1, "用户不正常(冻结)");
        }
    };

    public String getStatusStr() {
        return statusMap.get(this.status);
    }

}
