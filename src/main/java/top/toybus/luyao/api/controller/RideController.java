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
	 * 发布行程
	 */
	@LoginRequired
	@RequestMapping("/publish")
	public ResData publish(RideForm rideForm) {
		ResData resData = rideService.publishRide(rideForm);
		return resData;
	}

	/**
	 * 删除行程
	 */
	@LoginRequired
	@RequestMapping("/delete")
	public ResData delete(RideForm rideForm) {
		ResData resData = rideService.deleteRide(rideForm);
		return resData;
	}

	/**
	 * 修改行程
	 */
	@LoginRequired
	@RequestMapping("/update")
	public ResData update(RideForm rideForm) {
		ResData resData = rideService.updateRide(rideForm);
		return resData;
	}

	/**
	 * 行程列表
	 */
	@RequestMapping("/list")
	public ResData list(RideForm rideForm) {
		ResData resData = rideService.getRideList(rideForm);
		return resData;
	}

	/**
	 * 行程详情
	 */
	@RequestMapping("/detail")
	public ResData detail(RideForm rideForm) {
		ResData resData = rideService.getRideDetail(rideForm);
		return resData;
	}

	/**
	 * 新增行程模板
	 */
	@LoginRequired
	@RequestMapping("/addTemplate")
	public ResData addTemplate(RideForm rideForm) {
		ResData resData = rideService.addRideTemplate(rideForm);
		return resData;
	}

	/**
	 * 删除行程模板
	 */
	@LoginRequired
	@RequestMapping("/deleteTemplate")
	public ResData deleteTemplate(RideForm rideForm) {
		ResData resData = rideService.deleteTemplate(rideForm);
		return resData;
	}

	/**
	 * 修改行程模板
	 */
	@LoginRequired
	@RequestMapping("/updateTemplate")
	public ResData updateTemplate(RideForm rideForm) {
		ResData resData = rideService.updateTemplate(rideForm);
		return resData;
	}

	/**
	 * 行程模板列表
	 */
	@LoginRequired
	@RequestMapping("/listTemplate")
	public ResData listTemplate(RideForm rideForm) {
		ResData resData = rideService.listRideTemplate(rideForm);
		return resData;
	}

}
