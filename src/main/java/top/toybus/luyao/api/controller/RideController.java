package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.service.RideService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 顺丰车相关接口
 */
@RestController
@RequestMapping("/api/ride")
public class RideController {
	@Autowired
	private RideService rideService;

	/**
	 * 发布
	 */
	@LoginRequired
	@RequestMapping("/publish")
	public ResData publish(RideForm rideForm) {
		ResData resData = rideService.publishRide(rideForm);
		return resData;
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public ResData list(RideForm rideForm) {
		ResData resData = rideService.getRideList(rideForm);
		return resData;
	}

	/**
	 * 详情
	 */
	@RequestMapping("/detail")
	public ResData detail(RideForm rideForm) {
		ResData resData = rideService.getRideDetail(rideForm);
		return resData;
	}
}
