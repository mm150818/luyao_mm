package top.toybus.luyao.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;
import top.toybus.luyao.api.annotation.LoginRequired;
import top.toybus.luyao.api.formbean.TradeForm;
import top.toybus.luyao.api.service.TradeService;
import top.toybus.luyao.common.bean.ResData;

/**
 * 交易
 */
@Log4j
@RestController
@RequestMapping("/api/trade")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    /**
     * 阿里异步通知
     */
    @RequestMapping("/ali/asyncNotify")
    public String aliAsyncNotify(TradeForm tradeForm) {
        return tradeService.aliAsyncNotify(tradeForm);
    }

    /**
     * 微信异步通知
     */
    @RequestMapping("/wx/asyncNotify")
    public String wxAsyncNotify(TradeForm tradeForm) {
        return tradeService.wxAsyncNotify(tradeForm);
    }

    /**
     * 查询订单
     */
    @LoginRequired
    @RequestMapping("/orderQuery")
    public ResData orderQuery(TradeForm tradeForm) {
        ResData resData = null;
        try {
            resData = tradeService.orderQuery(tradeForm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resData = ResData.get();
            resData.setCode(-1).setMsg(e.getMessage());
        }
        return resData;
    }

}
