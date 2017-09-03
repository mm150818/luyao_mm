package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Message;
import top.toybus.luyao.api.formbean.MessageForm;
import top.toybus.luyao.api.formbean.RedPacketForm;
import top.toybus.luyao.api.repository.BalanceRepository;
import top.toybus.luyao.api.repository.MessageRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.common.util.RedPacketUtils;


@Service
@Transactional
public class RedPacketService {
		
	 @Autowired
	 private BalanceRepository balanceRepository;
	 
     RedPacketUtils redPacketUtils=new RedPacketUtils();
    


    /**
     * 发红包
     */
    public ResData send(RedPacketForm redPacketForm) {
        ResData resData = ResData.get();
        int mark=redPacketForm.getJudge();  //判断标志
        String money=null;                  //红包金额
        if(mark==1||mark==2) {              //1:发布消息；2:分享消息       	
        	money=redPacketUtils.random();
        	
        }
        if(money!=null) {
        	
               	
        }
        
        
        return resData;
    }
    
}
