package top.toybus.luyao.api.formbean;

import lombok.Data;
import top.toybus.luyao.api.entity.Car;

@Data
public class CarForm {
    private Car car = new Car();
    private int page = 1;
    private int size = 10;
}
