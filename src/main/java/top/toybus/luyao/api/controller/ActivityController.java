package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.formbean.ActivityForm;
import top.toybus.luyao.api.service.ActivityService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 活动
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public ResData list(ActivityForm activityForm) {
        ResData resData = activityService.list(activityForm);
        return resData;
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public ResData detail(ActivityForm activityForm) {
        ResData resData = activityService.detail(activityForm);
        return resData;
    }

    /**
     * 用户充值
     */
    @LoginRequired
    @RequestMapping("/charge")
    public ResData charge(ActivityForm activityForm) {
        ResData resData = activityService.charge(activityForm);
        return resData;
    }
}
