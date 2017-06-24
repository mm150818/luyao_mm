package top.toybus.luyao.sys.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.sys.entity.SysActivity;
import top.toybus.luyao.sys.formbean.SysActivityForm;
import top.toybus.luyao.sys.repository.SysActivityRepository;

@Service
@Transactional
public class SysActivityService {
    @Autowired
    private SysActivityRepository sysActivityRepository;

    public ResData list(SysActivityForm form) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(form);
        Page<SysActivity> pageActivity = sysActivityRepository.findAll(new Specification<SysActivity>() {
            @Override
            public Predicate toPredicate(Root<SysActivity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (form.getDeleted() != null) {
                    expressions.add(cb.equal(root.get("deleted"), form.getDeleted()));
                }
                query.orderBy(cb.desc(root.get("id")));
                return predicate;
            }
        }, pageable);
        resData.putAll(PageUtils.toMap("activityList", pageActivity));
        return resData;
    }

    public ResData detail(SysActivityForm form) {
        ResData resData = ResData.get();
        Long id = form.getId();
        SysActivity activity = sysActivityRepository.findOne(id);
        resData.put("activity", activity);
        return resData;
    }

    public ResData delete(SysActivityForm form) {
        ResData resData = ResData.get();
        Long id = form.getId();
        SysActivity activity = sysActivityRepository.findOne(id);
        activity.setDeleted(true);
        return resData;
    }

    public ResData add(SysActivityForm form) {
        ResData resData = ResData.get();
        SysActivity activity = new SysActivity();
        BeanUtils.copyProperties(form, activity);
        activity.setDeleted(false);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        activity = sysActivityRepository.save(activity);
        resData.put("activity", activity);
        return resData;
    }

}
