package top.toybus.luyao.api.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

		// 名字，时间，建议赏金，空座位，起点，途径一，途径二，终点
		if (StringUtils.isBlank(carForm.getName())) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入名字");
		} else if (carForm.getTime() == null) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入时间");
		} else if (carForm.getReward() == null) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入建议赏金");
		} else if (carForm.getSeats() == null) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入空座位");
		} else if (StringUtils.isBlank(carForm.getStartPoint())) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入起点");
		} else if (StringUtils.isBlank(carForm.getVia1())) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入途经一");
		} else if (StringUtils.isBlank(carForm.getVia2())) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入途经二");
		} else if (StringUtils.isBlank(carForm.getEndPoint())) {
			resData.setSc(ResData.SC_PARAM_ERROR).setMsg("请输入终点");
		} else {
			Car newCar = new Car();
			try {
				BeanUtils.copyProperties(carForm, newCar);
				newCar.setRemainSeats(0);
				newCar.setCreateTime(new Date());
				newCar.setUpdateTime(new Date());

				carRepository.save(newCar);
			} catch (Exception e) {
				e.printStackTrace();
				resData.setSc(1).setMsg("信息发布失败");
			}
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
		Car car = null;
		if (carForm.getId() != null) {
			car = carRepository.findOne(carForm.getId());
		}
		resData.put("car", car);
		return resData;
	}

}
