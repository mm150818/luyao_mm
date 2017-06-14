package top.toybus.luyao.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.sys.formbean.UserSysForm;
import top.toybus.luyao.sys.service.UserSysService;

/**
 * 用户
 */
@RestController
@RequestMapping("/sys/user")
public class UserSysController {
    @Autowired
    private UserSysService userSysService;

    /**
     * 用户车主认证
     */
    @RequestMapping("/verifyOwner")
    public ResData verifyOwner(UserSysForm userSysForm) {
        ResData resData = userSysService.verifyOwner(userSysForm);
        return resData;
    }

    /**
     * 用户列表
     */
    @RequestMapping("/list")
    public ResData list(UserSysForm userSysForm) {
        ResData resData = userSysService.list(userSysForm);
        return resData;
    }

}
