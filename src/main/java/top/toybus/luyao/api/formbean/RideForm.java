package top.toybus.luyao.api.formbean;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RideForm extends BaseForm {
	private Long id;

	private Boolean template;

	private String name;

	private String imgUrl;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;

	private BigDecimal reward;

	private Integer seats;

	private Integer remainSeats;

	private String startPoint;

	private String endPoint;

	private String via1;

	private String via2;

	private int page = 0;

	private int size = 10;
}
