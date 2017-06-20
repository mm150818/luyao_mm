package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.formbean.RideForm;
import top.toybus.luyao.api.formbean.UserForm;
import top.toybus.luyao.api.service.UserService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 用户
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @RequestMapping("/regist")
    public ResData regist(UserForm userForm) {
        ResData resData = userService.registUser(userForm);
        return resData;
    }

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public ResData login(UserForm userForm) {
        ResData resData = userService.loginUser(userForm);
        return resData;
    }

    /**
     * 用户登出
     */
    @LoginRequired
    @RequestMapping("/logout")
    public ResData logout(UserForm userForm) {
        ResData resData = userService.logout(userForm);
        return resData;
    }

    /**
     * 修改密码
     */
    @LoginRequired
    @RequestMapping("/updatePwd")
    public ResData updatePwd(UserForm userForm) {
        ResData resData = userService.updateUserPwd(userForm);
        return resData;
    }

    /**
     * 找回密码
     */
    @RequestMapping("/findPwd")
    public ResData findPwd(UserForm userForm) {
        ResData resData = userService.findUserPwd(userForm);
        return resData;
    }

    /**
     * 用户信息
     */
    @LoginRequired
    @RequestMapping("/info")
    public ResData info(UserForm userForm) {
        ResData resData = userService.getUser(userForm);
        return resData;
    }

    /**
     * 用户信息修改
     */
    @LoginRequired
    @RequestMapping("/updateInfo")
    public ResData updateInfo(UserForm userForm) {
        ResData resData = userService.updateInfo(userForm);
        return resData;
    }

    /**
     * 用户车主信息修改
     */
    @LoginRequired
    @RequestMapping("/updateOwnerInfo")
    public ResData updateOwnerInfo(UserForm userForm) {
        ResData resData = userService.updateOwnerInfo(userForm);
        return resData;
    }

    /**
     * 用户乘客订单列表
     */
    @LoginRequired
    @RequestMapping("/orderList")
    public ResData orderList(RideForm rideForm) {
        ResData resData = userService.orderList(rideForm);
        return resData;
    }

    /**
     * 用户司机订单列表
     */
    @LoginRequired
    @RequestMapping("/rideList")
    public ResData rideList(RideForm rideForm) {
        ResData resData = userService.rideList(rideForm);
        return resData;
    }

    /**
     * 用户账户明细列表
     */
    @LoginRequired
    @RequestMapping("/balanceList")
    public ResData balanceList(UserForm userForm) {
        ResData resData = userService.balanceList(userForm);
        return resData;
    }

    /**
     * 用户充值
     */
    @LoginRequired
    @RequestMapping("/charge")
    public ResData charge(UserForm userForm) {
        ResData resData = userService.charge(userForm);
        return resData;
    }

    /**
     * 用户提现
     */
    @LoginRequired
    @RequestMapping("/drawCash")
    public ResData drawCash(UserForm userForm) {
        ResData resData = userService.drawCash(userForm);
        return resData;
    }

}
