package top.toybus.luyao.api.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Car;
import top.toybus.luyao.api.formbean.CarForm;
import top.toybus.luyao.api.repository.CarRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.util.PageUtils;

@Service
@Transactional
public class CarService {
    @Autowired
    private CarRepository carRepository;

    /**
     * 发布车信息
     */
    public ResData publishCar(CarForm carForm) {
	ResData resData = new ResData();
	Car formCar = carForm.getCar();
	// 名字，时间，建议赏金，空座位，起点，途径一，途径二，终点
	if (StringUtils.isBlank(formCar.getName())) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入名字");
	} else if (formCar.getTime() == null) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入时间");
	} else if (formCar.getReward() == null) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入建议赏金");
	} else if (formCar.getSeats() == null) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入空座位");
	} else if (StringUtils.isBlank(formCar.getStartPoint())) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入起点");
	} else if (StringUtils.isBlank(formCar.getVia1())) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入途经一");
	} else if (StringUtils.isBlank(formCar.getVia2())) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入途经二");
	} else if (StringUtils.isBlank(formCar.getEndPoint())) {
	    resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入终点");
	} else {
	    formCar.setRemainSeats(0);
	    formCar.setCreateTime(new Date());
	    formCar.setUpdateTime(new Date());

	    carRepository.save(formCar);
	}
	return resData;
    }

    /**
     * 列表
     */
    public ResData getCarList(CarForm carForm) {
	ResData resData = new ResData();
	Pageable pageable = new PageRequest(carForm.getPage(), carForm.getSize());
	Page<Car> pageCar = carRepository.findAllBy(pageable);
	resData.putAll(PageUtils.toMap("carList", pageCar));
	return resData;
    }

    /**
     * 详情信息
     */
    public ResData getCarDetail(CarForm carForm) {
	ResData resData = new ResData();
	Car formCar = carForm.getCar();
	Car car = null;
	if (formCar.getId() != null) {
	    car = carRepository.findOne(formCar.getId());
	}
	resData.put("car", car);
	return resData;
    }

}
