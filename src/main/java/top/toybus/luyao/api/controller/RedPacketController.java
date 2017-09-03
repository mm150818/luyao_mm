package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.formbean.RedPacketForm;
import top.toybus.luyao.api.service.RedPacketService;
import top.toybus.luyao.common.bean.ResData;


/**
 * 红包
 */
@RestController
@RequestMapping("/api/redpacket")
public class RedPacketController {
    @Autowired
    private RedPacketService redPacketService;

    /**
     * 红包接口
     */
    @RequestMapping("/send")
    public ResData send(RedPacketForm redPacketForm) {
    	
    	ResData resData=redPacketService.send(redPacketForm);
    	
        return resData;
    }  
}
