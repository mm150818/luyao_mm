package top.toybus.luyao.api.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.service.RideService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 行程相关接口
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

    /**
     * 行程预定
     */
    @LoginRequired
    @RequestMapping("/order")
    public ResData order(RideForm rideForm) {
        ResData resData = rideService.order(rideForm);
        Payment payment = (Payment) resData.getData().get("payment");
        if (resData.getCode() == 0) { // 第一次下单成功
            Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    rideService.timeoutCancelOrder(payment.getOrderNo());
                }
            }, 5, TimeUnit.MINUTES);
        } else if (resData.getCode() == 3) { // 有未支付的订单，按正常请求处理
            resData.setCode(0);
        }
        return resData;
    }

    /**
     * 行程取消
     */
    @LoginRequired
    @RequestMapping("/cancel")
    public ResData cancel(RideForm rideForm) {
        ResData resData = rideService.cancel(rideForm);
        return resData;
    }

    /**
     * 行程结束
     */
    @LoginRequired
    @RequestMapping("/finish")
    public ResData finish(RideForm rideForm) {
        ResData resData = rideService.finish(rideForm);
        return resData;
    }

    /**
     * 每天00:01:00秒执行
     * 行程自动结束
     */
    // @Scheduled(initialDelay = 0, fixedDelay = Integer.MAX_VALUE)
    @Scheduled(cron = "0 1 0 * * ?")
    public void scheduledFinish() {
        rideService.autoFinish();
    }

}
