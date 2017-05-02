package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.formbean.CarForm;
import top.toybus.luyao.api.service.CarService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 顺丰车相关接口
 */
@RestController
@RequestMapping("/api/car")
public class CarController {
    @Autowired
    private CarService carService;

    /**
     * 发布
     */
    @RequestMapping("/publish")
    public ResData publish(CarForm carForm) {
	ResData resData = carService.publishCar(carForm);
	return resData;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public ResData list(CarForm carForm) {
	ResData resData = carService.getCarList(carForm);
	return resData;
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public ResData detail(CarForm carForm) {
	ResData resData = carService.getCarDetail(carForm);
	return resData;
    }
}
