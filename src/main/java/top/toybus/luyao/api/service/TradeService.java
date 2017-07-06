package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import top.toybus.luyao.api.entity.Activity;
import top.toybus.luyao.api.entity.Balance;
import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.api.formbean.TradeForm;
import top.toybus.luyao.api.repository.ActivityRepository;
import top.toybus.luyao.api.repository.BalanceRepository;
import top.toybus.luyao.api.repository.PaymentRepository;
import top.toybus.luyao.api.repository.UserRepository;
import top.toybus.luyao.api.repository.UserRideRepository;
import top.toybus.luyao.api.repository.VehicleRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.helper.TradeHelper;
import top.toybus.luyao.common.util.FormatUtils;

@Service
@Transactional
public class TradeService {
    @Autowired
    private UserRideRepository userRideRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private TradeHelper tradeHelper;
    @Autowired
    private SmsHelper smsHelper;

    /**
     * 检查用户订单和支付方式
     */
    private ResData checkOrderNo(TradeForm tradeForm) {
        ResData resData = ResData.get();
        Long orderNo = tradeForm.getOrderNo();
        if (orderNo == null || orderNo <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("订单号不能为空");
        }
        return resData;
    }

    /**
     * 检验数据
     */
    private boolean verifyAliData(Payment pay, String outTradeNo, String totalAmount, String sellerId, String appId) {
        return pay != null && pay.getOrderNo().toString().equals(outTradeNo)
                && FormatUtils.moneyCent2Yuan(pay.getTotalAmount()).equals(totalAmount)
                && tradeHelper.getTradeProps().getALI_SELLER_ID().equals(sellerId)
                && tradeHelper.getTradeProps().getALI_APP_ID().equals(appId);
    }

    /**
     * 支付宝异步通知
     */
    public String aliAsyncNotify() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        boolean signVerified = tradeHelper.verifyAliSign(request.getParameterMap());
        if (signVerified) {
            String tradeStatus = request.getParameter("trade_status");
            String outTradeNo = request.getParameter("out_trade_no");
            String tradeNo = request.getParameter("trade_no");
            String totalAmount = request.getParameter("total_amount");
            String sellerId = request.getParameter("seller_id");
            String appId = request.getParameter("app_id");

            Long orderNo = Long.valueOf(outTradeNo);
            Payment payment = paymentRepository.findByOrderNo(orderNo);
            if (this.verifyAliData(payment, outTradeNo, totalAmount, sellerId, appId)) {
                payment.setTradeNo(tradeNo);
                payment.setNotifyTime(LocalDateTime.now());

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    if (payment.getStatus() == 0) {
                        payment.setStatus(1);

                        Integer type = payment.getType();

                        Balance balance = new Balance(); // 收支明细
                        balance.setCreateTime(LocalDateTime.now());
                        balance.setMoney(payment.getTotalAmount());
                        balance.setPaymentId(payment.getId());
                        balance.setUserId(payment.getUserId());
                        balance.setWay(payment.getWay());

                        User user = userRepository.findOne(payment.getUserId());
                        if (type == 1) { // 行程订单
                            balance.setType(4); // 行程支出

                            UserRide userRide = userRideRepository.findByPayment(payment);
                            userRide.getRide().getOwner().setVehicle(
                                    vehicleRepository.findOne(userRide.getRide().getOwner().getVehicleId()));
                            smsHelper.sendOrderOkSms(user, userRide);
                        } else if (type == 2) { // 充值
                            balance.setType(1); // 充值

                            Long amount = payment.getTotalAmount();
                            // 关联活动
                            if (payment.getActivityId() != null) {
                                Activity activity = activityRepository.findOne(payment.getActivityId());
                                amount = activity.getAmount() + activity.getExtraAmount();

                                balance.setActivity(activity);
                            }

                            user.setBalance(user.getBalance() + amount);
                            user.setIncome(user.getIncome() + amount);
                        } else if (type == 4) { // 绑定账号
                            balance.setType(5); // 绑定支付宝账号

                            Long amount = payment.getTotalAmount();
                            user.setBalance(user.getBalance() + amount);
                            user.setIncome(user.getIncome() + amount);
                            String aliAccount = request.getParameter("buyer_id");
                            user.setAliAccount(aliAccount);
                        }

                        balanceRepository.save(balance);
                    }
                } else {
                    if (payment.getStatus() == 0) {
                        payment.setStatus(3);
                    }
                }
                return "success";
            }
        }
        return "failure";
    }

    /**
     * 微信异步通知处理
     */
    public String wxAsyncNotify() {
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> paramsMap = tradeHelper.getWxReqParamsMap();
        boolean signVerified = tradeHelper.verifyWxSign(paramsMap);
        Object returnCodeObj = paramsMap.get("return_code");
        returnMap.put("return_code", "FAIL");
        if (signVerified && "SUCCESS".equals(returnCodeObj)) {
            String outTradeNo = paramsMap.get("out_trade_no").toString();
            Long orderNo = Long.valueOf(outTradeNo);
            String resultCode = paramsMap.get("result_code").toString();
            String transactionId = paramsMap.get("transaction_id").toString();

            Payment payment = paymentRepository.findByOrderNo(orderNo);
            if (payment.getTotalAmount().toString().equals(paramsMap.get("total_fee"))) {
                payment.setTradeNo(transactionId);
                payment.setNotifyTime(LocalDateTime.now());

                if ("SUCCESS".equals(resultCode)) {
                    if (payment.getStatus() == 0) {
                        payment.setStatus(1);

                        Integer type = payment.getType();
                        if (type == 1) { // 行程订单
                            UserRide userRide = userRideRepository.findByPayment(payment);
                            User user = userRepository.findOne(userRide.getUserId());
                            smsHelper.sendOrderOkSms(user, userRide);
                        }
                    }
                } else {
                    if (payment.getStatus() == 0) {
                        payment.setStatus(3);

                    }
                }
                returnMap.put("return_code", "SUCCESS");
                returnMap.put("return_msg", "OK");
            } else {
                returnMap.put("return_msg", "total_fee invalid");
            }
        } else {
            returnMap.put("return_msg", "sign invalid");
        }

        try {
            String resultStr = Jackson2ObjectMapperBuilder.xml().build().writerFor(Map.class).withRootName("xml")
                    .writeValueAsString(returnMap);
            return resultStr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询订单
     */
    public ResData orderQuery(TradeForm tradeForm) {
        ResData resData = this.checkOrderNo(tradeForm);
        if (!resData.isOk()) {
            return resData;
        }
        Long orderNo = tradeForm.getOrderNo();
        Payment payment = paymentRepository.findByOrderNo(orderNo);
        if (payment == null) {
            return resData.setCode(1).setMsg("该订单不存在");
        }
        Integer way = payment.getWay();
        Map<String, Object> resultMap = tradeHelper.orderQuery(way, orderNo);
        resData.put("payment", payment);
        resData.putAll(resultMap);
        return resData;
    }
}
