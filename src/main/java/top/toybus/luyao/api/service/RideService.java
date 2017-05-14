package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.RideVia;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.repository.RideRepository;
import top.toybus.luyao.api.repository.RideViaRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.common.util.ValidatorUtils;

@Service
@Transactional
public class RideService {
	@Autowired
	private RideRepository rideRepository;
	@Autowired
	private RideViaRepository rideViaRepository;
	@Autowired
	private SmsHelper smsHelper;

	/**
	 * 发布车信息
	 */
	public ResData publishRide(RideForm rideForm) {
		ResData resData = ResData.get();
		User loginUser = rideForm.getLoginUser();
		if (!loginUser.getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		// 一个用户（车主认证过的）在当天0-24时间段内只能发布一个班次信息
		long count = rideRepository.countByPubToday(loginUser);
		if (count > 0) {
			return resData.setCode(2).setMsg("在当天0-24时间段内只能发布一个班次信息"); // err2
		}

		resData = this.checkRideForm(rideForm);
		if (resData.getCode() == 0) {
			Ride newRide = new Ride();
			try {
				BeanUtils.copyProperties(rideForm, newRide);
				newRide.setTemplate(false);
				newRide.setOwner(loginUser);
				newRide.setStatus(0);
				newRide.setCreateTime(LocalDateTime.now());
				newRide.setUpdateTime(LocalDateTime.now());

				List<RideVia> rideViaList = newRide.getRideViaList();
				newRide.setRideViaList(null);
				Ride ride = rideRepository.save(newRide);
				for (RideVia rideVia : rideViaList) {
					rideVia.setRideId(ride.getId());
					rideViaRepository.save(rideVia);
				}
				ride.setRideViaList(rideViaList);

				// 您的信息已发布成功（当用户发布班次信息成功时收到的提醒）
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("name", String.format("[%s]", ride.getName()));
				paramMap.put("time", ride.getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
				smsHelper.sendSms(loginUser.getMobile(), smsHelper.smsProperties.getTplRidePubOk(), paramMap);

				resData.put("ride", ride);
			} catch (Exception e) {
				resData.setCode(1).setMsg("车次信息发布失败");
			}
		}
		return resData;
	}

	/**
	 * 校验车次信息
	 */
	private ResData checkRideForm(RideForm rideForm) {
		ResData resData = ResData.get();
		// 名字，时间，建议赏金，空座位，起点，途径一，途径二，终点
		if (StringUtils.isBlank(rideForm.getName())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入名字");
		} else if (StringUtils.length(rideForm.getName()) > 15) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("名字格式不正确");
		} else if (rideForm.getTime() == null) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入时间");
		} else if (StringUtils.isBlank(rideForm.getAddress())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入出发详细地址");
		} else if (StringUtils.length(rideForm.getAddress()) > 15) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发详细地址格式不正确");
		} else if (rideForm.getReward() == null) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入建议赏金");
		} else if (ValidatorUtils.isNotMoney(rideForm.getReward().toString())) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("建议赏金格式不正确");
		} else if (rideForm.getSeats() == null || rideForm.getSeats() < 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入空座位数");
		} else if (rideForm.getRemainSeats() == null || rideForm.getRemainSeats() < 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入剩余座位数");
		} else if (rideForm.getRemainSeats() > rideForm.getSeats()) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("剩余座位数不能超过空座位数");
		} else {
			List<RideVia> rideViaList = rideForm.getRideViaList();
			if (rideViaList.isEmpty()) {
				resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点和终点");
			} else {
				RideVia startVia = rideViaList.get(0);
				if (StringUtils.isBlank(startVia.getPoint())) {
					resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
				} else if (StringUtils.length(startVia.getPoint()) > 15) {
					resData.setCode(ResData.C_PARAM_ERROR).setMsg("起点格式不正确");
				} else if (rideViaList.size() == 1) {
					resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
				} else {
					RideVia endVia = rideViaList.get(rideViaList.size() - 1);
					if (StringUtils.isBlank(endVia.getPoint())) {
						resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
					} else if (StringUtils.length(endVia.getPoint()) > 15) {
						resData.setCode(ResData.C_PARAM_ERROR).setMsg("终点格式不正确");
					} else {
						for (int i = 1; i < rideViaList.size() - 1; i++) {
							RideVia rideVia = rideViaList.get(i);
							if (StringUtils.isBlank(rideVia.getPoint())) {
								resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("请输入第%d个途径地点", i));
								break;
							} else if (StringUtils.length(rideVia.getPoint()) > 15) {
								resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("第%d个途径地点格式不正确", i));
								break;
							}
						}
					}
				}
			}
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
		ResData resData = ResData.get();
		User loginUser = rideForm.getLoginUser();
		if (!loginUser.getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		resData = this.checkRideForm(rideForm);
		if (resData.getCode() == 0) {
			Ride newRide = new Ride();
			try {
				BeanUtils.copyProperties(rideForm, newRide);
				newRide.setTemplate(true);
				newRide.setOwner(loginUser);
				newRide.setStatus(0);
				newRide.setCreateTime(LocalDateTime.now());
				newRide.setUpdateTime(LocalDateTime.now());

				List<RideVia> rideViaList = newRide.getRideViaList();
				newRide.setRideViaList(null);
				Ride ride = rideRepository.save(newRide);
				for (RideVia rideVia : rideViaList) {
					rideVia.setRideId(ride.getId());
					rideViaRepository.save(rideVia);
				}
				ride.setRideViaList(rideViaList);
				resData.put("rideTemplate", ride);
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
		if (!rideForm.getLoginUser().getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		PageRequest pageRequest = PageUtils.toPageRequest(rideForm);
		Page<Ride> pageRide = rideRepository.findAllByTemplateTrueAndOwner(rideForm.getLoginUser(), pageRequest);
		pageRide.getContent().forEach(ride -> ride.setRideUserList(Collections.emptyList()));
		resData.putAll(PageUtils.toMap("rideTemplateList", pageRide));
		return resData;
	}

	/**
	 * 删除车次
	 */
	public ResData deleteRide(RideForm rideForm) {
		ResData resData = ResData.get();
		User loginUser = rideForm.getLoginUser();
		if (!loginUser.getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		if (rideForm.getId() == null || rideForm.getId() <= 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车次ID");
		} else {
			Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
			if (ride == null || ride.getTemplate()) {
				resData.setCode(2).setMsg("该车次不存在"); // err2
			} else if (ride.getRideUserList().size() > 0) {
				resData.setCode(3).setMsg("该车次不能删除"); // err3
			} else {
				try {
					rideRepository.delete(ride);

					Map<String, String> paramMap = new HashMap<>();
					paramMap.put("name", String.format("[%s]", ride.getName()));
					smsHelper.sendSms(ride.getOwner().getMobile(), smsHelper.smsProperties.getTplRideDelOk(), paramMap);
				} catch (Exception e) {
					resData.setCode(4).setMsg("车次删除失败"); // err4
				}
			}
		}
		return resData;
	}

	/**
	 * 删除车次模板
	 */
	public ResData deleteTemplate(RideForm rideForm) {
		ResData resData = ResData.get();
		User loginUser = rideForm.getLoginUser();
		if (!loginUser.getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		if (rideForm.getId() == null || rideForm.getId() <= 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车次模板ID");
		} else {
			Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
			if (ride == null || !ride.getTemplate()) {
				resData.setCode(2).setMsg("该车次模板不存在"); // err2
			} else {
				try {
					rideRepository.delete(ride);
				} catch (Exception e) {
					resData.setCode(4).setMsg("车次模板删除失败"); // err4
				}
			}
		}
		return resData;
	}

	/**
	 * 车次信息修改
	 */
	public ResData updateRide(RideForm rideForm) {
		ResData resData = ResData.get();
		User loginUser = rideForm.getLoginUser();
		if (!loginUser.getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		if (rideForm.getId() == null || rideForm.getId() <= 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车次ID");
		} else {
			Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
			if (ride == null || ride.getTemplate()) {
				resData.setCode(2).setMsg("该车次不存在"); // err2
			} else if (ride.getRideUserList().size() > 0) {
				resData.setCode(3).setMsg("该车次不能修改"); // err3
			} else {
				// 检验表单
				if (StringUtils.isNotBlank(rideForm.getName())) {
					if (StringUtils.length(rideForm.getName()) > 15) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("名字格式不正确");
					}
					ride.setName(rideForm.getName());
				}
				if (rideForm.getTime() != null) {
					ride.setTime(rideForm.getTime());
				}
				if (StringUtils.isNotBlank(rideForm.getAddress())) {
					if (StringUtils.length(rideForm.getAddress()) > 15) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发详细地址格式不正确");
					}
					ride.setAddress(rideForm.getAddress());
				}
				if (rideForm.getReward() != null) {
					if (ValidatorUtils.isNotMoney(rideForm.getReward().toString())) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("建议赏金格式不正确");
					}
					ride.setReward(rideForm.getReward());
				}
				if (rideForm.getSeats() != null && rideForm.getSeats() > 0) {
					ride.setSeats(rideForm.getSeats());
				}
				if (rideForm.getRemainSeats() != null && rideForm.getRemainSeats() > 0) {
					if (rideForm.getSeats() == null) {
						if (rideForm.getRemainSeats() > ride.getSeats()) {
							return resData.setCode(ResData.C_PARAM_ERROR)
									.setMsg(String.format("剩余座位数不能超过%d", ride.getSeats()));
						}
					} else if (rideForm.getRemainSeats() > rideForm.getSeats()) {
						return resData.setCode(ResData.C_PARAM_ERROR)
								.setMsg(String.format("剩余座位数不能超过%d", rideForm.getSeats()));
					}
					ride.setRemainSeats(rideForm.getRemainSeats());
				}
				if (!rideForm.getRideViaList().isEmpty()) { // 修改途经地点
					List<RideVia> rideViaList = rideForm.getRideViaList();
					RideVia startVia = rideViaList.get(0);
					if (StringUtils.isBlank(startVia.getPoint())) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
					} else if (StringUtils.length(startVia.getPoint()) > 15) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("起点格式不正确");
					}
					if (rideViaList.size() == 1) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
					} else {
						RideVia endVia = rideViaList.get(rideViaList.size() - 1);
						if (StringUtils.isBlank(endVia.getPoint())) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
						} else if (StringUtils.length(endVia.getPoint()) > 15) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg("终点格式不正确");
						}
					}
					for (int i = 1; i < rideViaList.size() - 1; i++) {
						RideVia rideVia = rideViaList.get(i);
						if (StringUtils.isBlank(rideVia.getPoint())) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("请输入第%d个途径地点", i));
						} else if (StringUtils.length(rideVia.getPoint()) > 15) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("第%d个途径地点格式不正确", i));
						}
					}
				}

				ride.setUpdateTime(LocalDateTime.now());
				try {
					ride = rideRepository.save(ride);
					if (!rideForm.getRideViaList().isEmpty()) {
						rideViaRepository.delete(ride.getRideViaList()); // 删除先前的

						List<RideVia> rideViaList = rideForm.getRideViaList();
						for (RideVia rideVia : rideViaList) { // 保存现在的
							rideVia.setRideId(ride.getId());
							rideVia = rideViaRepository.save(rideVia);
						}
						ride.setRideViaList(rideViaList);
					}
					Map<String, String> paramMap = new HashMap<>();
					paramMap.put("name", String.format("[%s]", ride.getName()));
					paramMap.put("time", ride.getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
					smsHelper.sendSms(ride.getOwner().getMobile(), smsHelper.smsProperties.getTplRideUpdOk(), paramMap);

					resData.put("ride", ride);
				} catch (Exception e) {
					resData.setCode(4).setMsg("车次信息修改失败"); // err4
				}
			}
		}
		return resData;
	}

	/**
	 * 车次模板信息修改
	 */
	public ResData updateTemplate(RideForm rideForm) {
		ResData resData = ResData.get();
		User loginUser = rideForm.getLoginUser();
		if (!loginUser.getOwner()) {
			return resData.setCode(1).setMsg("您不是车主"); // err1
		}
		if (rideForm.getId() == null || rideForm.getId() <= 0) {
			resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车次模板ID");
		} else {
			Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
			if (ride == null || !ride.getTemplate()) {
				resData.setCode(2).setMsg("该车次模板不存在"); // err2
			} else {
				// 检验表单
				if (StringUtils.isNotBlank(rideForm.getName())) {
					if (StringUtils.length(rideForm.getName()) > 15) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("名字格式不正确");
					}
					ride.setName(rideForm.getName());
				}
				if (rideForm.getTime() != null) {
					ride.setTime(rideForm.getTime());
				}
				if (StringUtils.isNotBlank(rideForm.getAddress())) {
					if (StringUtils.length(rideForm.getAddress()) > 15) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发详细地址格式不正确");
					}
					ride.setAddress(rideForm.getAddress());
				}
				if (rideForm.getReward() != null) {
					if (ValidatorUtils.isNotMoney(rideForm.getReward().toString())) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("建议赏金格式不正确");
					}
					ride.setReward(rideForm.getReward());
				}
				if (rideForm.getSeats() != null && rideForm.getSeats() > 0) {
					ride.setSeats(rideForm.getSeats());
				}
				if (rideForm.getRemainSeats() != null && rideForm.getRemainSeats() > 0) {
					if (rideForm.getSeats() == null) {
						if (rideForm.getRemainSeats() > ride.getSeats()) {
							return resData.setCode(ResData.C_PARAM_ERROR)
									.setMsg(String.format("剩余座位数不能超过%d", ride.getSeats()));
						}
					} else if (rideForm.getRemainSeats() > rideForm.getSeats()) {
						return resData.setCode(ResData.C_PARAM_ERROR)
								.setMsg(String.format("剩余座位数不能超过%d", rideForm.getSeats()));
					}
					ride.setRemainSeats(rideForm.getRemainSeats());
				}
				if (!rideForm.getRideViaList().isEmpty()) { // 修改途经地点
					List<RideVia> rideViaList = rideForm.getRideViaList();
					RideVia startVia = rideViaList.get(0);
					if (StringUtils.isBlank(startVia.getPoint())) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
					} else if (StringUtils.length(startVia.getPoint()) > 15) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("起点格式不正确");
					}
					if (rideViaList.size() == 1) {
						return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
					} else {
						RideVia endVia = rideViaList.get(rideViaList.size() - 1);
						if (StringUtils.isBlank(endVia.getPoint())) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
						} else if (StringUtils.length(endVia.getPoint()) > 15) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg("终点格式不正确");
						}
					}
					for (int i = 1; i < rideViaList.size() - 1; i++) {
						RideVia rideVia = rideViaList.get(i);
						if (StringUtils.isBlank(rideVia.getPoint())) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("请输入第%d个途径地点", i));
						} else if (StringUtils.length(rideVia.getPoint()) > 15) {
							return resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("第%d个途径地点格式不正确", i));
						}
					}
				}

				ride.setUpdateTime(LocalDateTime.now());
				try {
					ride = rideRepository.save(ride);
					if (!rideForm.getRideViaList().isEmpty()) {
						rideViaRepository.delete(ride.getRideViaList()); // 删除先前的

						List<RideVia> rideViaList = rideForm.getRideViaList();
						for (RideVia rideVia : rideViaList) { // 保存现在的
							rideVia.setRideId(ride.getId());
							rideVia = rideViaRepository.save(rideVia);
						}
						ride.setRideViaList(rideViaList);
					}

					resData.put("rideTemplate", ride);
				} catch (Exception e) {
					resData.setCode(4).setMsg("车次模板信息修改失败"); // err4
				}
			}
		}
		return resData;
	}

}
