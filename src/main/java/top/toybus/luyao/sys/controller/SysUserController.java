package top.toybus.luyao.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.sys.formbean.SysUserForm;
import top.toybus.luyao.sys.service.SysUserService;

/**
 * 用户
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户车主认证
     */
    @RequestMapping("/verifyOwner")
    public ResData verifyOwner(SysUserForm userSysForm) {
        ResData resData = sysUserService.verifyOwner(userSysForm);
        return resData;
    }

    /**
     * 用户列表
     */
    @RequestMapping("/list")
    public ResData list(SysUserForm userSysForm) {
        ResData resData = sysUserService.list(userSysForm);
        return resData;
    }

    /**
     * 详情
     */
    @RequestMapping("detail")
    public ResData detail(SysUserForm userSysForm) {
        ResData resData = sysUserService.detail(userSysForm);
        return resData;
    }

}
