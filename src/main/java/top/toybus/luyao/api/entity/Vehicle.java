package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    private Long userId;

    private String no;

    private String model;

    @Column(name = "travel_img")
    private String travelImg;

    @Column(name = "driving_img")
    private String drivingImg;

    private String img;

    @Column(name = "ride_template_id")
    private Long rideTemplateId;

    @JsonIgnore
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @JsonIgnore
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public String getTravelImg() {
        return FileProperties.BASE_URL + this.travelImg;
    }

    public String getDrivingImg() {
        return FileProperties.BASE_URL + this.travelImg;
    }

    public String getImg() {
        return FileProperties.BASE_URL + this.img;
    }

}
