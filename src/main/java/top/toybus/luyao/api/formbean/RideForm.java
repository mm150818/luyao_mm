package top.toybus.luyao.api.formbean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import top.toybus.luyao.api.entity.RideVia;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RideForm extends BaseForm {
    private Long id;

    private Boolean template;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String startPoint;

    private String endPoint;

    private Long reward;

    private Integer seats;

    private List<RideVia> rideViaList = new ArrayList<>();

    private int page = 0;

    private int size = 10;

    private Long rideId;

    private Long rideViaId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Long orderNo;

    private Integer way;
}
