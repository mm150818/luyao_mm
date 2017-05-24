package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

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

}
