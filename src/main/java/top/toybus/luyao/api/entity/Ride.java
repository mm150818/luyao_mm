package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 顺风行程
 */
@Data
@Entity
@Table(name = "tb_ride")
@SuppressWarnings("serial")
public class Ride implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    private Boolean template;

    @JsonIgnoreProperties({ "owner", "balance", "status" })
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String startPoint;

    private String endPoint;

    private Long reward;

    private Integer seats;

    private Integer remainSeats;

    private Integer status;

    @OneToMany
    @JoinColumn(name = "ride_id", updatable = false)
    private List<RideVia> rideViaList = new ArrayList<>(0);

    @JsonIgnore
    private LocalDateTime createTime;

    @JsonIgnore
    private LocalDateTime updateTime;

    @JsonInclude(Include.NON_NULL)
    @OneToMany
    @JoinColumn(name = "ride_id", updatable = false)
    @Where(clause = "canceled = false")
    private List<RideUser> rideUserList = new ArrayList<>(0);

    public List<RideUser> getRideUserList() {
        return template ? null : rideUserList;
    }

    @JsonIgnore
    public String getStartEndPoint() {
        return this.startPoint + "—" + this.endPoint;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final Map<Integer, String> statusMap = new HashMap() {
        {
            put(0, "已发布");
            put(1, "已结束");
            put(2, "已取消");
        }
    };

    public String getStatusStr() {
        return statusMap.get(this.status);
    }
}
