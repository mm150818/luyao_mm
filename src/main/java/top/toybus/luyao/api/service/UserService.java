package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;

import top.toybus.luyao.api.entity.Balance;
import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.api.entity.Vehicle;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.formbean.UserForm;
import top.toybus.luyao.api.repository.BalanceRepository;
import top.toybus.luyao.api.repository.PaymentRepository;
import top.toybus.luyao.api.repository.RideRepository;
import top.toybus.luyao.api.repository.SmsRepository;
import top.toybus.luyao.api.repository.UserRepository;
import top.toybus.luyao.api.repository.UserRideRepository;
import top.toybus.luyao.api.repository.VehicleRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.helper.TradeHelper;
import top.toybus.luyao.common.util.FormatUtils;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.common.util.UUIDUtils;
import top.toybus.luyao.common.util.ValidatorUtils;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SmsRepository smsRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private UserRideRepository userRideRepository;
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
     * 注册用户
     */
    public ResData registUser(UserForm userForm) {
        ResData resData = this.checkMobilePwd(userForm);
        if (!resData.isOk()) {
            return resData;
        }
        resData = this.checkVerifyCode(userForm);
        if (!resData.isOk()) {
            return resData;
        }
        if (resData.isOk()) {
            // 获得最近发送的验证码
            Sms sms = smsRepository.findLastSendSms(userForm.getMobile());
            resData = this.checkSmsVerifyCode(sms, userForm); // err1-3
            if (resData.isOk()) {
                // 添加用户到数据库
                boolean exists = userRepository.existsUserByMobile(userForm.getMobile());
                if (exists) {
                    resData.setCode(4).setMsg("该手机号已经存在"); // err4
                } else {
                    User newUser = new User();
                    newUser.setMobile(userForm.getMobile());
                    newUser.setToken(UUIDUtils.randUUID());
                    newUser.setPassword(DigestUtils.md5Hex(userForm.getPassword()));
                    newUser.setNickname(StringUtils.substring(userForm.getMobile(), -4));
                    newUser.setHeadImg("img/default/user_head_img.jpg");
                    newUser.setBalance(0L);
                    newUser.setIncome(0L);
                    newUser.setDrawCash(0L);
                    newUser.setRideCount(0);
                    newUser.setStatus(0);
                    newUser.setOwner(0);
                    newUser.setCreateTime(LocalDateTime.now());
                    newUser.setUpdateTime(LocalDateTime.now());

                    User user = userRepository.save(newUser);
                    sms.setStatus(1); // 更新短信状态为已使用
                    sms.setUpdateTime(LocalDateTime.now());

                    resData.put("user", user);
                }
            }
        }
        return resData;
    }

    /**
     * 检查手机号，密码
     */
    private ResData checkMobilePwd(UserForm userForm) {
        ResData resData = new ResData();
        if (StringUtils.isBlank(userForm.getMobile())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入手机号");
        } else if (ValidatorUtils.isNotMobile(userForm.getMobile())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("手机号格式不正确");
        } else if (StringUtils.isBlank(userForm.getPassword())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入密码");
        } else if (ValidatorUtils.isNotPassword(userForm.getPassword())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("密码格式不正确");
        }
        return resData;
    }

    /**
     * 检查验证码
     */
    private ResData checkVerifyCode(UserForm userForm) {
        ResData resData = new ResData();
        if (StringUtils.isBlank(userForm.getVerifyCode())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入验证码");
        } else if (ValidatorUtils.isNotVerifyCode(userForm.getVerifyCode())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("验证码格式不正确");
        }
        return resData;
    }

    /**
     * 检查短信校验码
     * err1-3
     */
    private ResData checkSmsVerifyCode(Sms sms, UserForm userForm) {
        ResData resData = ResData.get();
        if (sms == null) {
            resData.setCode(1); // err1
            resData.setMsg("请发送短信验证码");
        } else if (!sms.getVerifyCode().equals(userForm.getVerifyCode())) {
            resData.setCode(2); // err2
            resData.setMsg("验证码不正确");
        } else if (LocalDateTime.now()
                .isAfter(sms.getCreateTime().plusMinutes(smsHelper.smsProperties.getValidMinutes()))) { // 超时
            sms.setStatus(2); // 更新短信状态为已过期
            sms.setUpdateTime(LocalDateTime.now());
            resData.setCode(3).setMsg("验证码已过期");  // err3
        }
        return resData;
    }

    /**
     * 用户登录
     */
    public ResData loginUser(UserForm userForm) {
        ResData resData = this.checkMobilePwd(userForm);
        if (resData.isOk()) {
            User user = userRepository.findUserByMobile(userForm.getMobile());
            if (user == null) {
                resData.setCode(1).setMsg("该手机号未注册"); // err1
            } else if (!user.getPassword().equals(DigestUtils.md5Hex(userForm.getPassword()))) {
                resData.setCode(2).setMsg("密码输入错误"); // err2
            } else {
                if (user.getStatus() == 1) { // err0
                    // resData.setCode(0).setMsg("用户已经登录");
                } else { // 更新用户状态为已登录
                    user.setToken(UUIDUtils.randUUID()); // 产生新的令牌
                    user.setStatus(1); // 已登录
                    user.setUpdateTime(LocalDateTime.now());
                }
                if (user.getVehicleId() != null) { // 如果是车主
                    Vehicle vehicle = vehicleRepository.findOne(user.getVehicleId());
                    user.setVehicle(vehicle);
                }

                user.setPassword(null); // password为null是，序列化包含token
                resData.put("user", user);
            }
        }
        return resData;
    }

    /**
     * 登出
     */
    public ResData logout(UserForm userForm) {
        ResData resData = ResData.get();
        User loginUser = userForm.getLoginUser();
        loginUser.setStatus(0);
        userRepository.save(loginUser);
        return resData;
    }

    /**
     * 修改密码
     */
    public ResData updateUserPwd(UserForm userForm) {
        ResData resData = ResData.get();
        if (StringUtils.isBlank(userForm.getOldPassword())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入原密码");
        } else if (ValidatorUtils.isNotPassword(userForm.getOldPassword())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("原密码格式不正确");
        } else if (StringUtils.isBlank(userForm.getPassword())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入新密码");
        } else if (ValidatorUtils.isNotPassword(userForm.getPassword())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("新密码格式不正确");
        }
        if (resData.isOk()) {
            User loginUser = userForm.getLoginUser();
            if (!loginUser.getPassword().equals(DigestUtils.md5Hex(userForm.getOldPassword()))) {
                resData.setCode(1).setMsg("原密码输入错误"); // err1
            } else {
                int count = userRepository.updateUserPwdById(loginUser.getId(),
                        DigestUtils.md5Hex(userForm.getPassword()), LocalDateTime.now());
                if (count != 1) {
                    resData.setCode(2).setMsg("密码修改失败"); // err2
                }
            }
        }
        return resData;
    }

    /**
     * 找回密码
     */
    public ResData findUserPwd(UserForm userForm) {
        ResData resData = new ResData();
        if (StringUtils.isBlank(userForm.getMobile())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入手机号");
        } else if (ValidatorUtils.isNotMobile(userForm.getMobile())) {
            resData.setCode(ResData.C_PARAM_ERROR).setMsg("手机号格式不正确");
        } else if (this.checkVerifyCode(userForm).isOk()) {
            // 获得最近发送的验证码
            Sms sms = smsRepository.findLastSendSms(userForm.getMobile());
            resData = this.checkSmsVerifyCode(sms, userForm); // err1-3
            if (resData.isOk()) {
                if (StringUtils.isBlank(userForm.getPassword())) {
                    resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入新密码");
                } else if (ValidatorUtils.isNotPassword(userForm.getPassword())) {
                    resData.setCode(ResData.C_PARAM_ERROR).setMsg("新密码格式不正确");
                }
                if (resData.isOk()) {
                    User user = userRepository.findUserByMobile(userForm.getMobile());
                    if (user == null) {
                        resData.setCode(4).setMsg("该手机号未注册"); // err4
                    } else {
                        int count = userRepository.updateUserPwdById(user.getId(),
                                DigestUtils.md5Hex(userForm.getPassword()), LocalDateTime.now());
                        if (count != 1) {
                            resData.setCode(5).setMsg("密码找回失败"); // err5
                        }
                        sms.setStatus(1); // 更新短信状态为已使用
                        sms.setUpdateTime(LocalDateTime.now());
                    }
                }
            }
        }
        return resData;
    }

    /**
     * 获得用户
     */
    @Transactional(readOnly = true)
    public ResData getUser(UserForm userForm) {
        ResData resData = ResData.get();
        User user = userForm.getLoginUser();
        if (user.getVehicleId() != null) { // 如果是车主
            Vehicle vehicle = vehicleRepository.findOne(user.getVehicleId());
            user.setVehicle(vehicle);
        }
        resData.put("user", user);
        return resData;
    }

    /**
     * 获得登录的用户
     */
    @Transactional(readOnly = true)
    public User getLoginUser(String token) {
        User user = userRepository.findUserByToken(token);
        return user;
    }

    /**
     * 修改用户信息
     */
    public ResData updateInfo(UserForm userForm) {
        ResData resData = ResData.get();
        User loginUser = userForm.getLoginUser();

        if (userForm.getHeadImg() != null) {
            if (ValidatorUtils.isNotImgPath(userForm.getHeadImg())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("头像地址格式不正确");
            }
            loginUser.setHeadImg(userForm.getHeadImg());
        }
        if (userForm.getNickname() != null) {
            if (ValidatorUtils.isNotNickname(userForm.getNickname())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("昵称格式不正确");
            }
            loginUser.setNickname(userForm.getNickname());
        }
        if (userForm.getSign() != null) {
            if (StringUtils.length(userForm.getSign()) > 50) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("个性签名格式不正确");
            }
            loginUser.setSign(userForm.getSign());
        }
        if (userForm.getSex() != null) {
            if (userForm.getSex() != 1 && userForm.getSex() != -1 && userForm.getSex() != 0) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("性别格式不正确");
            }
            loginUser.setSex(userForm.getSex());
        }
        if (userForm.getOccupation() != null) {
            if (StringUtils.length(userForm.getOccupation()) > 20) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("职业格式不正确");
            }
            loginUser.setOccupation(userForm.getOccupation());
        }
        if (userForm.getBirthday() != null) {
            loginUser.setBirthday(userForm.getBirthday());
        }

        loginUser.setUpdateTime(LocalDateTime.now());
        User user = userRepository.save(loginUser);

        resData.put("user", user);
        return resData;
    }

    /**
     * 修改车主信息
     */
    public ResData updateOwnerInfo(UserForm userForm) {
        ResData resData = ResData.get();
        User loginUser = userForm.getLoginUser();

        // 是否是新增
        boolean isNew = loginUser.getOwner() == 0;
        Vehicle vehicle = null;
        if (isNew) { // 不是车主，准备新增车辆信息
            vehicle = new Vehicle();
        } else { // 获取已经保存的车辆信息，准备修改
            vehicle = vehicleRepository.findOne(loginUser.getVehicleId());
        }
        if (userForm.getPlateNo() != null) {
            if (StringUtils.length(userForm.getPlateNo()) > 10) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("车牌号格式不正确");
            }
            vehicle.setPlateNo(userForm.getPlateNo());
        } else if (isNew) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车编号");
        }
        if (userForm.getModel() != null) {
            if (StringUtils.length(userForm.getModel()) > 20) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("车型格式不正确");
            }
            vehicle.setModel(userForm.getModel());
        } else if (isNew) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车型");
        }
        if (userForm.getTravelImg() != null) {
            if (ValidatorUtils.isNotImgPath(userForm.getTravelImg())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("行驶证图片格式不正确");
            }
            vehicle.setTravelImg(userForm.getTravelImg());
        } else if (isNew) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入行驶证图片");
        }
        if (userForm.getDrivingImg() != null) {
            if (ValidatorUtils.isNotImgPath(userForm.getDrivingImg())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("驾驶证图片格式不正确");
            }
            vehicle.setDrivingImg(userForm.getDrivingImg());
        } else if (isNew) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入驾驶证图片");
        }
        if (userForm.getImg() != null) {
            if (ValidatorUtils.isNotImgPath(userForm.getImg())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("车辆图片格式不正确");
            }
            vehicle.setImg(userForm.getImg());
        } else if (isNew) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入车辆图片");
        }
        if (userForm.getRideTemplateId() != null) {
            if (rideRepository.existsByTemplateTrueAndIdAndOwner(userForm.getRideTemplateId(), loginUser)) {
                vehicle.setRideTemplateId(userForm.getRideTemplateId());
            } else {
                return resData.setCode(1).setMsg("行程模板不存在"); // err1
            }
        }

        if (isNew) {
            vehicle.setCreateTime(LocalDateTime.now());
            String no = this.getVehicleNo(); // 生成车牌号
            vehicle.setNo(no);
        }
        vehicle.setUpdateTime(LocalDateTime.now());
        vehicle = vehicleRepository.save(vehicle);
        // 修改车辆信息，重新审核
        userRepository.updateUserOwnerById(loginUser.getId(), 2, vehicle.getId());

        resData.put("vehicle", vehicle);
        return resData;
    }

    /**
     * 生成车编号，行程号保留1000内的数字。也就是说从1001开始所有带4的数字不要。E1001(etc的) 普通的就K1002
     */
    private String getVehicleNo() {
        Vehicle vehicle = vehicleRepository.findFirstByOrderByCreateTimeDesc();
        String prevNo = null;
        if (vehicle == null) {
            prevNo = "E1000";
        } else {
            prevNo = vehicle.getNo();
        }
        prevNo = prevNo.substring(1);
        String nextNo = Integer.toString(Integer.valueOf(prevNo) + 1); // 数字+1
        nextNo = nextNo.replaceFirst("4", "5"); // 存在4，则替换为5
        return "E" + nextNo;
    }

    /**
     * 乘客订单列表
     */
    public ResData orderList(RideForm rideForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(rideForm);
        Page<UserRide> pageUserRide = userRideRepository.findAll(toUserRideSpecification(rideForm), pageable);
        pageUserRide.getContent().forEach(userRide -> userRide.getRide().setRideUserList(null));
        resData.putAll(PageUtils.toMap("userRideList", pageUserRide));
        return resData;
    }

    /**
     * 司机行程列表
     */
    public ResData rideList(RideForm rideForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(rideForm);
        Page<Ride> pageRide = rideRepository.findAll(toRideSpecification(rideForm), pageable);
        // 车的信息
        pageRide.forEach(ride -> ride.getOwner().setVehicle(vehicleRepository.findOne(ride.getOwner().getVehicleId())));
        resData.putAll(PageUtils.toMap("rideList", pageRide));
        return resData;
    }

    /**
     * 构建行程列表查询条件
     */
    private Specification<Ride> toRideSpecification(final RideForm rideForm) {
        return new Specification<Ride>() {
            @Override
            public Predicate toPredicate(Root<Ride> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                // join fetch
                root.fetch(root.getModel().getList("rideViaList"), JoinType.LEFT);

                // 不是模板
                expressions.add(cb.equal(root.get("template").as(Boolean.class), false));
                // 指定乘客
                expressions.add(cb.equal(root.get("owner").as(User.class), rideForm.getLoginUser()));
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
     * 构建行程列表查询条件
     */
    private Specification<UserRide> toUserRideSpecification(final RideForm rideForm) {
        return new Specification<UserRide>() {
            @Override
            public Predicate toPredicate(Root<UserRide> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                root.fetch(root.getModel().getSingularAttribute("ride", Ride.class), JoinType.LEFT);
                root.fetch(root.getModel().getSingularAttribute("payment", Payment.class), JoinType.LEFT);

                expressions.add(cb.equal(root.get("userId").as(Long.class), rideForm.getLoginUser().getId()));
                expressions.add(cb.notEqual(root.get("payment").get("status").as(Integer.class), 2));
                // 行程开始时间
                if (rideForm.getStartTime() != null) {
                    expressions.add(cb.greaterThanOrEqualTo(root.get("ride").get("time").as(LocalDateTime.class),
                            rideForm.getStartTime()));
                }
                // 行程结束时间
                if (rideForm.getEndTime() != null) {
                    expressions.add(cb.lessThanOrEqualTo(root.get("ride").get("time").as(LocalDateTime.class),
                            rideForm.getEndTime()));
                }
                // 行程出发地
                if (StringUtils.isNotBlank(rideForm.getStartPoint())) {
                    expressions.add(cb.like(root.get("ride").get("startPoint").as(String.class),
                            "%" + rideForm.getStartPoint() + "%"));
                }
                // 行程目的地
                if (StringUtils.isNotBlank(rideForm.getEndPoint())) {
                    expressions.add(cb.like(root.get("ride").get("endPoint").as(String.class),
                            "%" + rideForm.getEndPoint() + "%"));
                }
                // id desc
                query.orderBy(cb.desc(root.get("id")));
                return predicate;
            }
        };
    }

    /**
     * 账户明细
     */
    public ResData balanceList(UserForm userForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(userForm);
        Page<Balance> pageBalance = balanceRepository.findAll((new Specification<Balance>() {
            @Override
            public Predicate toPredicate(Root<Balance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(cb.equal(root.get("userId").as(Long.class), userForm.getLoginUser().getId()));
                // 开始时间
                if (userForm.getStartTime() != null) {
                    expressions.add(cb.greaterThanOrEqualTo(root.get("createTime").as(LocalDateTime.class),
                            userForm.getStartTime()));
                }
                // 结束时间
                if (userForm.getEndTime() != null) {
                    expressions.add(cb.lessThanOrEqualTo(root.get("createTime").as(LocalDateTime.class),
                            userForm.getEndTime()));
                }
                query.orderBy(cb.desc(root.get("id")));
                return predicate;
            }
        }), pageable);
        resData.putAll(PageUtils.toMap("balanceList", pageBalance));
        return resData;
    }

    /**
     * 充值
     */
    public ResData charge(UserForm userForm) {
        ResData resData = ResData.get();
        if (userForm.getMoney() == null || userForm.getMoney() <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入充值金额");
        }
        if (userForm.getWay() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请选择支付方式");
        } else if (userForm.getWay() != 1 && userForm.getWay() != 2) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付方式格式不正确");
        }

        Integer way = userForm.getWay();
        Long money = userForm.getMoney();
        Long orderNo = UUIDUtils.getOrderNo();
        User loginUser = userForm.getLoginUser();

        Map<String, Object> resultMap = tradeHelper.unifiedOrder(way, orderNo, "马洲路遥-充值", money, 5);

        Payment payment = new Payment();
        payment.setCreateTime(LocalDateTime.now());
        payment.setTotalAmount(userForm.getMoney());
        payment.setOrderNo(orderNo);
        payment.setWay(userForm.getWay());
        payment.setStatus(0);
        payment.setType(2);
        payment.setUserId(loginUser.getId());

        payment = paymentRepository.save(payment);

        resData.put("payment", payment);
        resData.putAll(resultMap);
        return resData;
    }

    /**
     * 提现
     */
    public ResData drawCash(UserForm userForm) {
        ResData resData = ResData.get();
        if (userForm.getMoney() == null || userForm.getMoney() <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入充值金额");
        }
        if (userForm.getWay() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请选择支付方式");
        } else if (userForm.getWay() != 1 && userForm.getWay() != 2) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付方式格式不正确");
        }
        Integer way = userForm.getWay();
        if (way == 1) {
            if (StringUtils.isBlank(userForm.getAccount())) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入支付宝收款方账户");
            } else if (StringUtils.length(userForm.getAccount()) > 100) {
                return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付宝收款方账户格式不正确");
            }
        }
        User loginUser = userForm.getLoginUser();
        Long money = userForm.getMoney();
        Long balance = loginUser.getBalance(); // 账户余额
        if (balance.longValue() < money.longValue()) {
            return resData.setCode(1).setMsg("账户余额目前为" + FormatUtils.moneyCent2Yuan(balance) + "元"); // err1
        }
        String account = userForm.getAccount();
        Long orderNo = UUIDUtils.getOrderNo();

        Map<String, Object> resultMap = tradeHelper.transfer(way, orderNo, "马洲路遥-提现", money, account);

        Payment payment = new Payment();
        payment.setCreateTime(LocalDateTime.now());
        payment.setTotalAmount(userForm.getMoney());
        payment.setOrderNo(orderNo);
        payment.setWay(userForm.getWay());
        payment.setType(3);
        payment.setUserId(loginUser.getId());

        boolean payOk = false;
        if (way == 1) {
            AlipayFundTransToaccountTransferResponse response = (AlipayFundTransToaccountTransferResponse) resultMap
                    .get("payResult");
            payment.setTradeNo(response.getOrderId());
            if (response.isSuccess()) {
                payment.setStatus(1);
                payment.setNotifyTime(LocalDateTime.now());

                payOk = true;
            } else {
                payment.setStatus(3);
            }
        } else if (way == 2) {
//            Object object = resultMap.get("payResult");
        }

        if (payOk) {
            payment = paymentRepository.save(payment);

            loginUser.setBalance(loginUser.getBalance() - money);
            loginUser.setDrawCash(loginUser.getDrawCash() + money);
            userRepository.save(loginUser);

            Balance balance2 = new Balance();
            balance2.setCreateTime(LocalDateTime.now());
            balance2.setMoney(money);
            balance2.setPaymentId(payment.getId());
            balance2.setType(2);
            balance2.setUserId(loginUser.getId());
            balance2.setWay(way);

            balanceRepository.save(balance2);
        }

        resData.put("payment", payment);
        resData.putAll(resultMap);

        return resData;
    }

}
