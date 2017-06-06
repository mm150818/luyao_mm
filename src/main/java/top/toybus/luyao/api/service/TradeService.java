package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.entity.RideVia;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.api.formbean.TradeForm;
import top.toybus.luyao.api.repository.PaymentRepository;
import top.toybus.luyao.api.repository.UserRideRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.helper.TradeHelper;

@Service
@Transactional
public class TradeService {
    @Autowired
    private UserRideRepository userRideRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private TradeHelper tradeHelper;
    @Autowired
    private SmsHelper smsHelper;

    /**
     * 检查用户订单和支付方式
     */
    private ResData checkOrderNoAndWay(TradeForm tradeForm) {
        ResData resData = ResData.get();
        Long orderNo = tradeForm.getOrderNo();
        if (orderNo == null || orderNo <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("订单号不能为空");
        }
        if (tradeForm.getWay() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请选择支付方式");
        } else if (tradeForm.getWay() != 1 && tradeForm.getWay() != 2) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付方式格式不正确");
        }
        return resData;
    }

    /**
     * 检验数据
     */
    private boolean verifyAliData(Payment pay, String outTradeNo, String totalAmount, String sellerId, String appId) {
        return pay != null && pay.getOrderNo().toString().equals(outTradeNo)
                && pay.getTotalAmount().toString().equals(totalAmount)
                && tradeHelper.getTradeProps().getALI_SELLER_ID().equals(sellerId)
                && tradeHelper.getTradeProps().getALI_APP_ID().equals(appId);
    }

    /**
     * 发送订购成功短信
     */
    private void sendOrderOkSms(TradeForm tradeForm, UserRide userRide) {
        // 5月12日19点30分中潭路4号口不见不散（当用户预约并支付成功时收到的提醒）
        User loginUser = tradeForm.getLoginUser();
        Map<String, String> paramMap = new HashMap<>();
        RideVia rideVia = userRide.getRideVia();
        paramMap.put("name", String.format("[%s]", userRide.getRide().getStartEndPoint()));
        paramMap.put("time", rideVia.getTime().format(DateTimeFormatter.ofPattern("M月d日HH点mm分")));
        paramMap.put("address", rideVia.getPoint());
        smsHelper.sendSms(loginUser.getMobile(), smsHelper.getSmsProperties().getTplOrderOk(), paramMap);
    }

    /**
     * 支付宝异步通知
     */
    public String aliAsyncNotify(TradeForm tradeForm) {
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
                        if (type == 1) { // 行程订单
                            UserRide userRide = userRideRepository.findByPayment(payment);
                            sendOrderOkSms(tradeForm, userRide);
                        }
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
    public String wxAsyncNotify(TradeForm tradeForm) {
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
                            sendOrderOkSms(tradeForm, userRide);
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
     * 统一下单
     */
    /*public ResData unifiedOrder(TradeForm tradeForm) {
        ResData resData = this.checkOrderNoAndWay(tradeForm);
        if (!resData.isOk()) {
            return resData;
        }
        Long orderNo = tradeForm.getOrderNo();
        UserRide userRide = userRideRepository.findByOrderNo(orderNo);
        if (userRide == null) {
            return resData.setCode(1).setMsg("该订单不存在"); // err1
        }
        Long totalAmount = userRide.getRide().getReward();
        Integer way = tradeForm.getWay();
        String body = "马洲路遥-预定行程";
        if (way == 1) {
            String result = tradeHelper.aliAppPay(orderNo.toString(), body, String.format("%.2f", totalAmount / 100.0));
            resData.put("orderStr", result);
        } else if (way == 2) {
            Map<String, Object> resultMap = tradeHelper.wxUnifiedorder(orderNo.toString(), body,
                    totalAmount.toString());
            resData.putAll(resultMap);
        }
    
        // 保存对账单
        Payment pay = new Payment();
        pay.setType(1);
        pay.setTargetId(userRide.getId());
        pay.setOutTradeNo(orderNo);
        pay.setTotalAmount(totalAmount);
        pay.setWay(tradeForm.getWay()); // 1支付宝支付,2微信支付
        pay.setCreateTime(LocalDateTime.now());
        paymentRepository.save(pay);
    
        userRide.setStatus(2); // 订单状态：处理中
    
        return resData;
    }*/

    /**
     * 查询订单
     */
    public ResData orderQuery(TradeForm tradeForm) {
        ResData resData = this.checkOrderNoAndWay(tradeForm);
        if (!resData.isOk()) {
            return resData;
        }
        String outTradeNo = tradeForm.getOrderNo().toString();
        Integer way = tradeForm.getWay();
        if (way == 1) {
            String result = tradeHelper.AliQuery(outTradeNo);
            resData.put("result", result);
        } else if (way == 2) {
            Map<String, Object> resultMap = tradeHelper.wxOrderQuery(outTradeNo);
            resData.putAll(resultMap);
        }
        return resData;
    }
}
