package top.toybus.luyao.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.sys.formbean.SysActivityForm;
import top.toybus.luyao.sys.service.SysActivityService;

/**
 * 活动
 */
@RestController
@RequestMapping("/sys/activity")
public class SysActivityController {
    @Autowired
    private SysActivityService sysActivityService;

    /**
     * 用户列表
     */
    @RequestMapping("/list")
    public ResData list(SysActivityForm form) {
        ResData resData = sysActivityService.list(form);
        return resData;
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public ResData detail(SysActivityForm form) {
        ResData resData = sysActivityService.detail(form);
        return resData;
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public ResData delete(SysActivityForm form) {
        ResData resData = sysActivityService.delete(form);
        return resData;
    }

    /**
     * 添加
     */
    @RequestMapping("/add")
    public ResData add(SysActivityForm form) {
        ResData resData = sysActivityService.add(form);
        return resData;
    }

}
