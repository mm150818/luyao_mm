package top.toybus.luyao.api.formbean;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CarForm {
	private Long id;
	private String name;
	private String headimgUrl;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;
	private BigDecimal reward;
	private Integer seats;
	private Integer remainSeats;
	private String startPoint;
	private String endPoint;
	private String via1;
	private String via2;

	private int page = 1;
	private int size = 10;
}
