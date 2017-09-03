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
import top.toybus.luyao.api.repository.MessageRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.util.PageUtils;


@Service
@Transactional
public class MessageService {
    
	@Autowired
    private MessageRepository messageRepository;

    /**
     * 消息列表
     */
    public ResData list(MessageForm messageForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(messageForm);
        String mark_id=String.valueOf(messageForm.getMark_id());
        
        Page<Message> pageMessage = messageRepository.findAll(new Specification<Message>() {
            @Override
            public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
               /* if (StringUtils.isNotBlank(mark_id)) {
                    expressions.add(cb.like(root.get("mark_id"), "%" + messageForm.getMark() + "%"));
               }*/
                
                if (mark_id.equals("1")||mark_id.equals("2")) {
                    expressions.add(cb.equal(root.get("mark_id"), messageForm.getMark_id()));
                }
                query.orderBy(cb.desc(root.get("create_time")));
                
                
                return predicate;
            }
            
        }, pageable);
        resData.putAll(PageUtils.toMap("messageList", pageMessage));

        return resData;
    }
    
    /**
     * 消息详情
     */  
    public ResData detail(MessageForm messageForm) {
        ResData resData = ResData.get();
        Long id = messageForm.getId();
        Message msg = messageRepository.findOne(id);
        resData.put("message", msg);
        return resData;
    }
    
    /**
     * 添加消息
     */   
    public ResData add(MessageForm messageForm) {

    	ResData resData = ResData.get();
        Message msg= new Message();
        msg.setUser_id(messageForm.getUser_id());
        msg.setUser_name(messageForm.getUser_name());
        msg.setMark_id(messageForm.getMark_id());
        msg.setMark(messageForm.getMark());
        msg.setMessage(messageForm.getMessage());
        msg.setCreate_time(LocalDateTime.now());
        
        msg=messageRepository.save(msg);
        
        resData.put("message",msg);
        return resData;
    }
    
    /**
     * 删除消息
     */
    
    public ResData delete(MessageForm messageForm) {

    	ResData resData = ResData.get();
        Long id = messageForm.getId();
        messageRepository.delete(id);
        return resData;
    }
    
}
