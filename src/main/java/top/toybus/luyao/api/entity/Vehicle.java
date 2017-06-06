
package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import top.toybus.luyao.common.properties.FileProperties;

/**
 * 车信息
 */
@Data
@Entity
@Table(name = "tb_vehicle")
@SuppressWarnings("serial")
public class Vehicle implements Serializable {
    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    private String no;

    private String plateNo;

    private String model;

    private String travelImg;

    private String drivingImg;

    private String img;

    private Long rideTemplateId;

    @JsonIgnore
    private LocalDateTime createTime;

    @JsonIgnore
    private LocalDateTime updateTime;

    public String getTravelImg() {
        return FileProperties.BASE_URL + this.travelImg;
    }

    public String getDrivingImg() {
        return FileProperties.BASE_URL + this.drivingImg;
    }

    public String getImg() {
        return FileProperties.BASE_URL + this.img;
    }

}
