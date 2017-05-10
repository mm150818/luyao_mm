package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.repository.RideRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.util.PageUtils;

@Service
@Transactional
public class RideService {
	@Autowired
	private RideRepository rideRepository;

	/**
	 * 发布车信息
	 */
	public ResData publishRide(RideForm rideForm) {
		ResData resData = this.verifyRideForm(rideForm);
		if (resData.getCode() == 0) {
			Ride newRide = new Ride();
			try {
				BeanUtils.copyProperties(rideForm, newRide);
				newRide.setTemplate(false);
				newRide.setUser(rideForm.getLoginUser());
				newRide.setCreateTime(LocalDateTime.now());
				newRide.setUpdateTime(LocalDateTime.now());
				rideRepository.save(newRide);

			} catch (Exception e) {
				resData.setCode(1).setMsg("车次信息发布失败");
			}
		}
		return resData;
	}

	/**
	 * 校验车次信息
	 */
	private ResData verifyRideForm(RideForm rideForm) {
		ResData resData = ResData.get();
		// 名字，时间，建议赏金，空座位，起点，途径一，途径二，终点
		if (StringUtils.isBlank(rideForm.getName())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入名字");
		} else if (rideForm.getTime() == null) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入时间");
		} else if (rideForm.getReward() == null) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入建议赏金");
		} else if (rideForm.getSeats() == null) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入空座位");
		} else if (StringUtils.isBlank(rideForm.getStartPoint())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
		} else if (StringUtils.isBlank(rideForm.getVia1())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入途经一");
		} else if (StringUtils.isBlank(rideForm.getVia2())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入途经二");
		} else if (StringUtils.isBlank(rideForm.getEndPoint())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
		}
		return resData;
	}

	/**
	 * 列表
	 */
	public ResData getRideList(RideForm rideForm) {
		ResData resData = new ResData();
		PageRequest pageRequest = PageUtils.toPageRequest(rideForm);
		Page<Ride> pageRide = rideRepository.findAllByTemplateFalse(pageRequest);
		resData.putAll(PageUtils.toMap("rideList", pageRide));
		return resData;
	}

	/**
	 * 详情信息
	 */
	public ResData getRideDetail(RideForm rideForm) {
		ResData resData = new ResData();
		if (rideForm.getId() == null || rideForm.getId() <= 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车次ID");
		} else {
			Ride ride = rideRepository.findOne(rideForm.getId());
			resData.put("ride", ride);
		}
		return resData;
	}

	/**
	 * 新增车次模板
	 */
	public ResData addRideTemplate(RideForm rideForm) {
		ResData resData = this.verifyRideForm(rideForm);
		if (resData.getCode() == 0) {
			Ride newRide = new Ride();
			try {
				BeanUtils.copyProperties(rideForm, newRide);
				newRide.setTemplate(true);
				newRide.setUser(rideForm.getLoginUser());
				newRide.setCreateTime(LocalDateTime.now());
				newRide.setUpdateTime(LocalDateTime.now());
				rideRepository.save(newRide);
			} catch (Exception e) {
				resData.setCode(1).setMsg("车次模板新增失败");
			}
		}
		return resData;
	}

	/**
	 * 车次模板列表
	 */
	public ResData listRideTemplate(RideForm rideForm) {
		ResData resData = ResData.get();
		PageRequest pageRequest = PageUtils.toPageRequest(rideForm);
		Page<Ride> pageRide = rideRepository.findAllByTemplateTrueAndUser(rideForm.getLoginUser(), pageRequest);
		pageRide.getContent().forEach(ride -> ride.setRideUserList(Collections.emptyList()));
		resData.putAll(PageUtils.toMap("rideTemplateList", pageRide));
		return resData;
	}

}
