package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.toybus.luyao.api.formbean.MessageForm;
import top.toybus.luyao.api.service.MessageService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 消息
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    /**
     * 消息列表
     */
    @RequestMapping("/list")
    public ResData list(MessageForm messageForm) {
        ResData resData = messageService.list(messageForm);
        return resData;
    }

    /**
     * 消息详情
     */
    @RequestMapping("/detail")
    public ResData detail(MessageForm messageForm) {
        ResData resData = messageService.detail(messageForm);
        return resData;
    }
    
    /**
     * 添加消息
     */
    @RequestMapping("/add")
    public ResData add(MessageForm messageForm) {
        ResData resData = messageService.add(messageForm);
        return resData;
    }

    /**
     * 删除消息
     */
    @RequestMapping("/delete")
    public ResData delete(MessageForm messageForm) {
        ResData resData = messageService.delete(messageForm);
        return resData;
    }
}
