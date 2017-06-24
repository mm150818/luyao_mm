package top.toybus.luyao.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.api.entity.Activity;
import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.formbean.ActivityForm;
import top.toybus.luyao.api.repository.ActivityRepository;
import top.toybus.luyao.api.repository.BalanceRepository;
import top.toybus.luyao.api.repository.PaymentRepository;
import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.TradeHelper;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.common.util.UUIDUtils;

@Service
@Transactional
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private TradeHelper tradeHelper;

    public ResData list(ActivityForm activityForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(activityForm);
        Page<Activity> pageActivity = activityRepository.findAll(new Specification<Activity>() {
            @Override
            public Predicate toPredicate(Root<Activity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(cb.equal(root.get("deleted"), false));
                query.orderBy(cb.desc(root.get("id")));
                return predicate;
            }
        }, pageable);
        resData.putAll(PageUtils.toMap("activityList", pageActivity));
        return resData;
    }

    public ResData detail(ActivityForm activityForm) {
        ResData resData = ResData.get();
        Long id = activityForm.getId();
        if (id == null || id <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入活动ID");
        }
        Activity activity = activityRepository.findOne(id);
        if (activity.getDeleted()) {
            return resData.setCode(1).setMsg("该活动已结束");
        }
        resData.put("activity", activity);
        return resData;
    }

    public ResData charge(ActivityForm activityForm) {
        ResData resData = ResData.get();
        Long id = activityForm.getId();
        if (id == null || id <= 0) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请输入活动ID");
        }
        if (activityForm.getWay() == null) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("请选择支付方式");
        } else if (activityForm.getWay() != 1 && activityForm.getWay() != 2) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("支付方式格式不正确");
        }

        User loginUser = activityForm.getLoginUser();
        Activity activity = activityRepository.findOne(id);

        long count = balanceRepository.countByUserIdAndActivity(loginUser.getId(), activity);
        if (count >= activity.getTotal()) {
            return resData.setCode(1).setMsg("该活动最多只能参加" + activity.getTotal() + "次");
        }

        Integer way = activityForm.getWay();
        Long money = activity.getAmount();
        Long orderNo = UUIDUtils.getOrderNo();

        money = Math.round(money.longValue() * activity.getDiscount() / 100.0);
        Map<String, Object> resultMap = tradeHelper.unifiedOrder(way, orderNo, "马洲路遥-活动充值", money, 5);

        Payment payment = new Payment();
        payment.setCreateTime(LocalDateTime.now());
        payment.setTotalAmount(money);
        payment.setOrderNo(orderNo);
        payment.setWay(activityForm.getWay());
        payment.setStatus(0);
        payment.setType(2); // 充值
        payment.setUserId(loginUser.getId());
        payment.setActivityId(activity.getId());

        payment = paymentRepository.save(payment);

        resData.put("payment", payment);
        resData.putAll(resultMap);
        return resData;
    }

}
