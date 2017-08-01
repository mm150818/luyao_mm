package top.toybus.luyao.api.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Balance;
import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.RideVia;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.api.entity.Vehicle;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.repository.BalanceRepository;
import top.toybus.luyao.api.repository.PaymentRepository;
import top.toybus.luyao.api.repository.RideRepository;
import top.toybus.luyao.api.repository.RideViaRepository;
import top.toybus.luyao.api.repository.UserRepository;
import top.toybus.luyao.api.repository.UserRideRepository;
import top.toybus.luyao.api.repository.VehicleRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.helper.TradeHelper;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.common.util.UUIDUtils;
import top.toybus.luyao.common.util.ValidatorUtils;

@Service
@Transactional
public class RideService {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RideViaRepository rideViaRepository;
    @Autowired
    private UserRideRepository userRideRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SmsHelper smsHelper;
    @Autowired
    private TradeHelper tradeHelper;

    /**
     * 发布车信息
     */
    public ResData publishRide(RideForm rideForm) {
        ResData resData = ResData.get();
        User loginUser = rideForm.getLoginUser();
        if (loginUser.isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        // 一个用户（车主认证过的）在当天0-24时间段内只能发布一个班次信息
        long count = rideRepository.countByOwnerAndTime(loginUser, rideForm.getTime());
        if (count > 0) {
            return resData.setCode(2).setMsg("在当天0-24点时间段内只能发布一个班次信息"); // err2
        }

        resData = this.checkRideForm(rideForm);
        if (resData.isOk()) {
            Ride newRide = new Ride();
            try {
                BeanUtils.copyProperties(rideForm, newRide);
                newRide.setTemplate(false);
                newRide.setOwner(loginUser);
                newRide.setRemainSeats(rideForm.getSeats());
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
                paramMap.put("name", String.format("[%s]", ride.getStartEndPoint()));
                paramMap.put("time", ride.getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
                smsHelper.sendSms(loginUser.getMobile(), smsHelper.smsProperties.getTplRidePubOk(), paramMap);

                ride.setRideUserList(null);
                resData.put("ride", ride);
            } catch (Exception e) {
                resData.setCode(-1).setMsg("行程信息发布失败"); // err-1
            }
        }
        return resData;
    }

    /**
     * 校验行程信息
     */
    private ResData checkRideForm(RideForm rideForm) {
        ResData resData = ResData.get();
        if (rideForm.getTime() == null) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入时间");
        } else if (StringUtils.isBlank(rideForm.getStartPoint())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入出发地");
        } else if (StringUtils.length(rideForm.getStartPoint()) > 10) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发地格式不正确");
        } else if (StringUtils.isBlank(rideForm.getEndPoint())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入目的地");
        } else if (StringUtils.length(rideForm.getEndPoint()) > 10) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("目的地格式不正确");
        } else if (rideForm.getReward() == null) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入建议赏金");
        } else if (ValidatorUtils.isNotMoney(rideForm.getReward().toString())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("建议赏金格式不正确");
        } else if (rideForm.getSeats() == null || rideForm.getSeats() <= 0) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入座位数");
        } else {
            List<RideVia> rideViaList = rideForm.getRideViaList();
            if (rideViaList.isEmpty()) {
                resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点和终点");
            } else {
                RideVia startVia = rideViaList.get(0);
                if (StringUtils.isBlank(startVia.getPoint())) {
                    resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
                } else if (StringUtils.length(startVia.getPoint()) > 30) {
                    resData.setCode(ResData.C_PARAM_ERROR).setMsg("起点格式不正确");
                } else if (rideViaList.size() == 1) {
                    resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
                } else {
                    RideVia endVia = rideViaList.get(rideViaList.size() - 1);
                    if (StringUtils.isBlank(endVia.getPoint())) {
                        resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
                    } else if (StringUtils.length(endVia.getPoint()) > 30) {
                        resData.setCode(ResData.C_PARAM_ERROR).setMsg("终点格式不正确");
                    } else {
                        for (int i = 1; i < rideViaList.size() - 1; i++) {
                            RideVia rideVia = rideViaList.get(i);
                            if (StringUtils.isBlank(rideVia.getPoint())) {
                                resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("请输入第%d个途径地点", i));
                                break;
                            } else if (StringUtils.length(rideVia.getPoint()) > 30) {
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
        if (StringUtils.length(rideForm.getStartPoint()) > 10) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发地格式不正确");
        }
        if (StringUtils.length(rideForm.getEndPoint()) > 10) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("目的地格式不正确");
        }
        Pageable pageable = PageUtils.toPageRequest(rideForm);
        // Page<Ride> pageRide = rideRepository.findAllByTemplateFalse(pageRequest);
        Page<Ride> pageRide = rideRepository.findAll(toSpecification(rideForm), pageable);
        pageRide.forEach(ride -> {
            User owner = ride.getOwner();
            if (owner.getVehicleId() != null) {
                owner.setVehicle(vehicleRepository.findOne(owner.getVehicleId()));
            }
        });
        resData.putAll(PageUtils.toMap("rideList", pageRide));
        return resData;
    }

    /**
     * 构建行程列表查询条件
     */
    private Specification<Ride> toSpecification(final RideForm rideForm) {
        return new Specification<Ride>() {
            @Override
            public Predicate toPredicate(Root<Ride> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                // 不是模板
                expressions.add(cb.equal(root.get("template").as(Boolean.class), false));
                // 时间
                if (rideForm.getTime() != null) {
                    expressions
                            .add(cb.greaterThanOrEqualTo(root.get("time").as(LocalDateTime.class), rideForm.getTime()));
                    expressions
                            .add(cb.lessThan(root.get("time").as(LocalDateTime.class), rideForm.getTime().plusDays(1)));
                }
                // 出发地
                if (StringUtils.isNotBlank(rideForm.getStartPoint())) {
                    expressions.add(
                            cb.like(root.get("startPoint").as(String.class), "%" + rideForm.getStartPoint() + "%"));
                }
                // 目的地
                if (StringUtils.isNotBlank(rideForm.getEndPoint())) {
                    expressions.add(cb.like(root.get("endPoint").as(String.class), "%" + rideForm.getEndPoint() + "%"));
                }
                // id desc
                query.orderBy(cb.desc(root.get("time").as(LocalDateTime.class)));

                return predicate;
            }
        };
    }

    /**
     * 详情信息
     */
    public ResData getRideDetail(RideForm rideForm) {
        ResData resData = new ResData();
        if (rideForm.getId() == null || rideForm.getId() <= 0) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程ID");
        } else {
            Ride ride = rideRepository.findOne(rideForm.getId());
            if (ride == null) {
                return resData.setCode(1).setMsg("该行程不存在");
            }
            User owner = ride.getOwner();
            if (owner.getVehicleId() != null) {
                Vehicle vehicle = vehicleRepository.findOne(owner.getVehicleId());
                owner.setVehicle(vehicle);
            }
            resData.put("ride", ride);
        }
        return resData;
    }

    /**
     * 新增行程模板
     */
    public ResData addRideTemplate(RideForm rideForm) {
        ResData resData = ResData.get();
        User loginUser = rideForm.getLoginUser();
        if (loginUser.isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        resData = this.checkRideForm(rideForm);
        if (resData.isOk()) {
            Ride newRide = new Ride();
            try {
                BeanUtils.copyProperties(rideForm, newRide);
                newRide.setTemplate(true);
                newRide.setOwner(loginUser);
                newRide.setRemainSeats(rideForm.getSeats());
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
                resData.setCode(-1).setMsg("行程模板新增失败"); // err-1
            }
        }
        return resData;
    }

    /**
     * 行程模板列表
     */
    public ResData listRideTemplate(RideForm rideForm) {
        ResData resData = ResData.get();
        if (rideForm.getLoginUser().isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        Pageable pageable = PageUtils.toPageRequest(rideForm);
        Page<Ride> pageRide = rideRepository.findAllByTemplateTrueAndOwner(rideForm.getLoginUser(), pageable);
        pageRide.getContent().forEach(ride -> ride.setRideUserList(null));
        resData.putAll(PageUtils.toMap("rideTemplateList", pageRide));
        return resData;
    }

    /**
     * 删除行程
     */
    public ResData deleteRide(RideForm rideForm) {
        ResData resData = ResData.get();
        User loginUser = rideForm.getLoginUser();
        if (loginUser.isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        if (rideForm.getId() == null || rideForm.getId() <= 0) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程ID");
        } else {
            Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
            if (ride == null || ride.getTemplate()) {
                resData.setCode(2).setMsg("该行程不存在"); // err2
            } else if (ride.getRideUserList().size() > 0) {
                resData.setCode(3).setMsg("该行程不能删除"); // err3
            } else {
                try {
                    rideRepository.delete(ride);

                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("name", String.format("[%s]", ride.getStartEndPoint()));
                    smsHelper.sendSms(ride.getOwner().getMobile(), smsHelper.smsProperties.getTplRideDelOk(), paramMap);
                } catch (Exception e) {
                    resData.setCode(-1).setMsg("行程删除失败"); // err-1
                }
            }
        }
        return resData;
    }

    /**
     * 删除行程模板
     */
    public ResData deleteTemplate(RideForm rideForm) {
        ResData resData = ResData.get();
        User loginUser = rideForm.getLoginUser();
        if (loginUser.isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        if (rideForm.getId() == null || rideForm.getId() <= 0) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程模板ID");
        } else {
            Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
            if (ride == null || !ride.getTemplate()) {
                resData.setCode(2).setMsg("该行程模板不存在"); // err2
            } else {
                try {
                    rideRepository.delete(ride);
                } catch (Exception e) {
                    resData.setCode(-1).setMsg("行程模板删除失败"); // err-1
                }
            }
        }
        return resData;
    }

    /**
     * 行程信息修改
     */
    public ResData updateRide(RideForm rideForm) {
        ResData resData = ResData.get();
        User loginUser = rideForm.getLoginUser();
        if (loginUser.isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        if (rideForm.getId() == null || rideForm.getId() <= 0) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程ID");
        } else {
            Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
            if (ride == null || ride.getTemplate()) {
                resData.setCode(2).setMsg("该行程不存在"); // err2
            } else if (ride.getRideUserList().size() > 0) {
                resData.setCode(3).setMsg("该行程不能修改"); // err3
            } else {
                // 检验表单
                if (rideForm.getTime() != null) {
                    ride.setTime(rideForm.getTime());
                }
                if (StringUtils.isNotBlank(rideForm.getStartPoint())) {
                    if (StringUtils.length(rideForm.getStartPoint()) > 10) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发地格式不正确");
                    }
                    ride.setStartPoint(rideForm.getStartPoint());
                }
                if (StringUtils.isNotBlank(rideForm.getEndPoint())) {
                    if (StringUtils.length(rideForm.getEndPoint()) > 10) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("目的地格式不正确");
                    }
                    ride.setEndPoint(rideForm.getEndPoint());
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
                if (!rideForm.getRideViaList().isEmpty()) { // 修改途经地点
                    List<RideVia> rideViaList = rideForm.getRideViaList();
                    RideVia startVia = rideViaList.get(0);
                    if (StringUtils.isBlank(startVia.getPoint())) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
                    } else if (StringUtils.length(startVia.getPoint()) > 30) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("起点格式不正确");
                    }
                    if (rideViaList.size() == 1) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
                    } else {
                        RideVia endVia = rideViaList.get(rideViaList.size() - 1);
                        if (StringUtils.isBlank(endVia.getPoint())) {
                            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
                        } else if (StringUtils.length(endVia.getPoint()) > 30) {
                            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("终点格式不正确");
                        }
                    }
                    for (int i = 1; i < rideViaList.size() - 1; i++) {
                        RideVia rideVia = rideViaList.get(i);
                        if (StringUtils.isBlank(rideVia.getPoint())) {
                            return resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("请输入第%d个途径地点", i));
                        } else if (StringUtils.length(rideVia.getPoint()) > 30) {
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
                    paramMap.put("name", String.format("[%s]", ride.getStartEndPoint()));
                    paramMap.put("time", ride.getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
                    smsHelper.sendSms(ride.getOwner().getMobile(), smsHelper.smsProperties.getTplRideUpdOk(), paramMap);

                    resData.put("ride", ride);
                } catch (Exception e) {
                    resData.setCode(-1).setMsg("行程信息修改失败"); // err-1
                }
            }
        }
        return resData;
    }

    /**
     * 行程模板信息修改
     */
    public ResData updateTemplate(RideForm rideForm) {
        ResData resData = ResData.get();
        User loginUser = rideForm.getLoginUser();
        if (loginUser.isNotOwner()) {
            return resData.setCode(1).setMsg("您不是车主"); // err1
        }
        if (rideForm.getId() == null || rideForm.getId() <= 0) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程模板ID");
        } else {
            Ride ride = rideRepository.findOneByIdAndOwner(rideForm.getId(), loginUser);
            if (ride == null || !ride.getTemplate()) {
                resData.setCode(2).setMsg("该行程模板不存在"); // err2
            } else {
                // 检验表单
                if (rideForm.getTime() != null) {
                    ride.setTime(rideForm.getTime());
                }
                if (StringUtils.isNotBlank(rideForm.getStartPoint())) {
                    if (StringUtils.length(rideForm.getStartPoint()) > 10) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("出发地格式不正确");
                    }
                    ride.setStartPoint(rideForm.getStartPoint());
                }
                if (StringUtils.isNotBlank(rideForm.getEndPoint())) {
                    if (StringUtils.length(rideForm.getEndPoint()) > 10) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("目的地格式不正确");
                    }
                    ride.setEndPoint(rideForm.getEndPoint());
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
                if (!rideForm.getRideViaList().isEmpty()) { // 修改途经地点
                    List<RideVia> rideViaList = rideForm.getRideViaList();
                    RideVia startVia = rideViaList.get(0);
                    if (StringUtils.isBlank(startVia.getPoint())) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入起点");
                    } else if (StringUtils.length(startVia.getPoint()) > 30) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("起点格式不正确");
                    }
                    if (rideViaList.size() == 1) {
                        return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
                    } else {
                        RideVia endVia = rideViaList.get(rideViaList.size() - 1);
                        if (StringUtils.isBlank(endVia.getPoint())) {
                            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入终点");
                        } else if (StringUtils.length(endVia.getPoint()) > 30) {
                            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("终点格式不正确");
                        }
                    }
                    for (int i = 1; i < rideViaList.size() - 1; i++) {
                        RideVia rideVia = rideViaList.get(i);
                        if (StringUtils.isBlank(rideVia.getPoint())) {
                            return resData.setCode(ResData.C_PARAM_ERROR).setMsg(String.format("请输入第%d个途径地点", i));
                        } else if (StringUtils.length(rideVia.getPoint()) > 30) {
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
                    resData.setCode(-1).setMsg("行程模板信息修改失败"); // err-1
                }
            }
        }
        return resData;
    }

    /**
     * 预定行程
     */
    public ResData order(RideForm rideForm) {
        ResData resData = ResData.get();
        if (rideForm.getRideId() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程ID");
        }
        if (rideForm.getSeats() == null || rideForm.getSeats() < 1) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入座位数");
        }
        if (rideForm.getRideViaId() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入途径地点ID");
        }
        Ride ride = rideRepository.findOne(rideForm.getRideId());
        if (ride == null) {
            return resData.setCode(1).setMsg("预定的行程不存在"); // err1
        }
        Optional<RideVia> optionalRideVia = ride.getRideViaList().stream()
                .filter(rideVia -> rideVia.getId().longValue() == rideForm.getRideViaId().longValue()).findFirst();
        RideVia rideVia = null;
        if (optionalRideVia.isPresent()) {
            rideVia = optionalRideVia.get();
        }
        if (rideVia == null) {
            return resData.setCode(2).setMsg("途经地点不存在"); // err2
        }
        User loginUser = rideForm.getLoginUser();
        // 最后一个订单
        UserRide userRide = userRideRepository.findFirstByUserIdAndRideOrderByIdDesc(loginUser.getId(), ride);
        if (userRide != null && userRide.getPayment().getStatus() == 0) { // 当前行程下有已创建的订单
            if (userRide.getSeats() + ride.getRemainSeats() < rideForm.getSeats()) {
                return resData.setCode(4)
                        .setMsg(String.format("所选行程目前还剩余%d个座位", userRide.getSeats() + ride.getRemainSeats())); // err4
            }
            if (rideForm.getWay() == null) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请选择支付方式");
            } else if (rideForm.getWay() != 1 && rideForm.getWay() != 2 && rideForm.getWay() != 3) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付方式格式不正确");
            }
            if (rideForm.getWay() == 3 && loginUser.getBalance() < userRide.getTotalAmount()) {
                return resData.setCode(5).setMsg("账户余额不足"); // err5
            }
            ride.setRemainSeats(userRide.getSeats() + ride.getRemainSeats() - rideForm.getSeats()); // 修改剩余座位数
            userRide.setSeats(rideForm.getSeats()); // 修改订单座位数
            userRide.setUpdateTime(LocalDateTime.now());

            Payment payment = userRide.getPayment();
            payment.setWay(rideForm.getWay()); // 修改支付方式
            payment.setTotalAmount(userRide.getTotalAmount()); // 修改订单总金额

            resData.put("payment", payment);
            if (rideForm.getWay() == 3) { // 余额扣款
                loginUser.setBalance(loginUser.getBalance() - payment.getTotalAmount());
                userRepository.save(loginUser);
                payment.setStatus(1); // 已支付

                Balance balance = new Balance(); // 收支明细
                balance.setCreateTime(LocalDateTime.now());
                balance.setMoney(payment.getTotalAmount());
                balance.setPaymentId(payment.getId());
                balance.setUserId(payment.getUserId());
                balance.setWay(payment.getWay());
                balance.setType(4); // 行程支出
                balanceRepository.save(balance);

                smsHelper.sendOrderOkSms(loginUser, userRide);
                return resData; // OK
            } else {
                long millis = Duration.of(5, ChronoUnit.MINUTES).toMillis()
                        - Duration.between(payment.getCreateTime(), LocalDateTime.now()).toMillis();
                if (millis >= 0) {
                    resData.setCode(0).put("remainPayMillis", millis);
                }
                int mins = (int) (millis / 1000 / 60); // 剩余支付分钟数
                if (mins < 1) { // 如果剩余支付分钟数为0，则设置为1分钟后超时
                    mins = 1;
                }
                Map<String, Object> orderMap = tradeHelper.unifiedOrder(payment.getWay(), payment.getOrderNo(),
                        "马洲路遥-行程支付", payment.getTotalAmount(), mins);
                resData.putAll(orderMap);
            }
            return resData.setCode(3).setMsg("当前行程下您有一个订单未支付"); // err3
        }
        // 新建行程订单
        if (ride.getRemainSeats() < rideForm.getSeats()) {
            return resData.setCode(4).setMsg(String.format("所选行程目前还剩余%d个座位", ride.getRemainSeats())); // err4
        }
        if (rideForm.getWay() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请选择支付方式");
        } else if (rideForm.getWay() != 1 && rideForm.getWay() != 2 && rideForm.getWay() != 3) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付方式格式不正确");
        }

        userRide = new UserRide();
        userRide.setUserId(loginUser.getId());
        userRide.setRide(ride);
        userRide.setSeats(rideForm.getSeats());
        userRide.setRideVia(rideVia);
        userRide.setConfirmed(false);
        userRide.setCanceled(false);
        userRide.setCreateTime(LocalDateTime.now());
        userRide.setUpdateTime(LocalDateTime.now());

        if (rideForm.getWay() == 3 && loginUser.getBalance() < userRide.getTotalAmount()) {
            return resData.setCode(5).setMsg("账户余额不足"); // err5
        }

        Integer way = rideForm.getWay();
        Long orderNo = UUIDUtils.getOrderNo();
        Long totalAmount = userRide.getTotalAmount();
        // 账单
        Payment payment = new Payment();
        payment.setCreateTime(LocalDateTime.now());
        payment.setTotalAmount(totalAmount);
        payment.setOrderNo(orderNo);
        payment.setWay(way);
        payment.setStatus(0);
        payment.setUserId(loginUser.getId());
        payment.setType(1);

        payment = paymentRepository.save(payment);
        userRide.setPayment(payment);

        userRide = userRideRepository.save(userRide); // 保存订单
        ride.setRemainSeats(ride.getRemainSeats() - userRide.getSeats()); // 修改剩余座位数
        ride.setUpdateTime(LocalDateTime.now());

        if (rideForm.getWay() == 3) { // 余额扣款
            loginUser.setBalance(loginUser.getBalance() - payment.getTotalAmount());
            userRepository.save(loginUser);
            payment.setStatus(1); // 已支付

            Balance balance = new Balance(); // 收支明细
            balance.setCreateTime(LocalDateTime.now());
            balance.setMoney(payment.getTotalAmount());
            balance.setPaymentId(payment.getId());
            balance.setUserId(payment.getUserId());
            balance.setWay(payment.getWay());
            balance.setType(4); // 行程支出
            balanceRepository.save(balance);

            User owner = userRide.getRide().getOwner();
            owner.setVehicle(vehicleRepository.findOne(owner.getVehicleId()));
            smsHelper.sendOrderOkSms(loginUser, userRide);
        } else {
            Map<String, Object> orderMap = tradeHelper.unifiedOrder(way, orderNo, "马洲路遥-行程支付", totalAmount, 5);
            resData.putAll(orderMap);
        }

        resData.put("payment", payment);
        return resData;
    }

    /**
     * 自动超时支付关闭
     */
    public void timeoutCancelOrder(Long orderNo) {
        Payment payment = paymentRepository.findByOrderNo(orderNo);
        if (payment.getStatus() == 0) { // 0已创建(未支付)
            payment.setStatus(2); // 用户(超时)已取消
            // paymentRepository.save(payment);

            UserRide userRide = userRideRepository.findByPayment(payment);
            userRide.setCanceled(true); // 订单取消
            // userRideRepository.save(userRide);

            Ride ride = userRide.getRide(); // 行程座位解锁
            ride.setRemainSeats(ride.getRemainSeats() + userRide.getSeats());

            // 关闭支付
            tradeHelper.closeOrder(payment.getWay(), orderNo);
        }
    }

    /**
     * 行程结束
     */
    public ResData finish(RideForm rideForm) {
        ResData resData = ResData.get();
        Long orderNo = rideForm.getOrderNo();
        if (orderNo == null || orderNo <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行车订单号");
        }
        Payment payment = paymentRepository.findByOrderNo(orderNo);
        if (payment == null) {
            return resData.setCode(1).setMsg("该行程订单不存在"); // err1
        }
        if (payment.getStatus() != 1) { // 未支付
            return resData.setCode(2).setMsg("该行程订单未支付"); // err2
        }
        UserRide userRide = userRideRepository.findByPayment(payment);
        if (userRide.getConfirmed()) {
            return resData.setCode(3).setMsg("该行程已结束"); // err3
        }
        User loginUser = rideForm.getLoginUser();
        userRide.setConfirmed(true); // 已结束
        loginUser.setRideCount(loginUser.getRideCount() + 1);  // 拼车次数+1
        userRepository.save(loginUser);

        Ride ride = userRide.getRide();
        User owner = ride.getOwner();
        Long totalAmount = payment.getTotalAmount();
        totalAmount -= tradeHelper.tradeProps.getCommission(); // 去掉佣金
        // 车主收入明细
        Balance balance = new Balance();
        balance.setCreateTime(LocalDateTime.now());
        balance.setMoney(totalAmount);
        balance.setType(3); // 行程收入
        balance.setUserId(owner.getId());
        balance.setPaymentId(payment.getId());

        balance = balanceRepository.save(balance);
        owner.setBalance(owner.getBalance() + totalAmount);
        owner.setIncome(owner.getIncome() + totalAmount);

        return resData;
    }

    /**
     * 取消行程
     */
    public ResData cancel(RideForm rideForm) {
        ResData resData = ResData.get();
        Long orderNo = rideForm.getOrderNo();
        if (orderNo == null || orderNo <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行程订单号");
        }
        // 获得行程账单
        Payment payment = paymentRepository.findByOrderNo(orderNo);
        if (payment == null) {
            return resData.setCode(1).setMsg("该行程订单不存在"); // err1
        }
        Integer status = payment.getStatus(); // 状态，0已创建(未支付)，1已支付，2用户已取消，3已关闭/支付失败，4已退款
        if (status == 2) {
            return resData.setCode(2).setMsg("该行程订单已取消"); // err2
        }
        UserRide userRide = userRideRepository.findByPayment(payment);
        if (userRide.getConfirmed()) {
            return resData.setCode(3).setMsg("该行程已结束"); // err3
        }
        if (status == 1) {
            // 暂时不支持退款
            return resData.setCode(3).setMsg("抱歉，暂时不支持退单！"); // err3
        }
        payment.setStatus(2);
        userRide.setCanceled(true);

        Integer way = payment.getWay();
        Ride ride = userRide.getRide();
        ride.setRemainSeats(ride.getRemainSeats() + userRide.getSeats());
        Map<String, Object> resultMap = null;
        if (status == 0) {
            // 关闭支付
            resultMap = tradeHelper.closeOrder(way, orderNo);
        } else if (status == 1) {
            // 支付退款

        }
        resData.putAll(resultMap);
        return resData;
    }

    /**
     * 行程自动结束
     */
    // @Async
    public void autoFinish() {
        Pageable pageable = new PageRequest(0, 10);
        // 查询所有已经支付了的但是乘客未确认的行程订单
        Page<UserRide> userRidePage = userRideRepository.findAllByConfirmedFalseAndPayment_StatusIs(1, pageable);
        while (userRidePage.getNumberOfElements() > 0) {
            userRidePage.forEach(userRide -> {
                userRide.setConfirmed(true); // 已结束

                Ride ride = userRide.getRide();
                Payment payment = userRide.getPayment();
                User owner = ride.getOwner();
                Long totalAmount = payment.getTotalAmount();
                totalAmount -= tradeHelper.tradeProps.getCommission(); // 去掉佣金

                // 车主收入明细
                Balance balance = new Balance();
                balance.setCreateTime(LocalDateTime.now());
                balance.setMoney(totalAmount);
                balance.setType(3); // 行程收入
                balance.setUserId(owner.getId());
                balance.setPaymentId(payment.getId());

                balance = balanceRepository.save(balance);
                owner.setBalance(owner.getBalance() + totalAmount);
                owner.setIncome(owner.getIncome() + totalAmount);
            });
            userRideRepository.flush();
            userRidePage = userRideRepository.findAllByConfirmedFalseAndPayment_StatusIs(1, pageable);
        }
    }

}
