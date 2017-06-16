
package top.toybus.luyao.sys.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import top.toybus.luyao.common.properties.FileProperties;

/**
 * 车信息
 */
@Data
@Entity
@Table(name = "tb_vehicle")
@SuppressWarnings("serial")
public class SysVehicle implements Serializable {
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public String getTravelImg() {
        if (this.travelImg == null) {
            return null;
        }
        return FileProperties.BASE_URL + this.travelImg;
    }

    public String getDrivingImg() {
        if (this.drivingImg == null) {
            return null;
        }
        return FileProperties.BASE_URL + this.drivingImg;
    }

    public String getImg() {
        if (this.img == null) {
            return null;
        }
        return FileProperties.BASE_URL + this.img;
    }

}
