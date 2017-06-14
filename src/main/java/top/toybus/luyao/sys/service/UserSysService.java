package top.toybus.luyao.sys.service;

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

import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
import top.toybus.luyao.common.util.PageUtils;
import top.toybus.luyao.sys.entity.UserSys;
import top.toybus.luyao.sys.formbean.UserSysForm;
import top.toybus.luyao.sys.repository.UserSysRepository;

@Service
@Transactional
public class UserSysService {
    @Autowired
    private UserSysRepository userSysRepository;
    @Autowired
    private SmsHelper smsHelper;

    /**
     * 认证车主
     */
    public ResData verifyOwner(UserSysForm userSysForm) {
        ResData resData = ResData.get();
        if (userSysForm.getOwner() == null || userSysForm.getOwner() != 1 && userSysForm.getOwner() != 3) {
            return resData.setCode(ResData.C_PARAM_ERROR).setMsg("是否认证通过格式不正确");
        }
        String token = userSysForm.getToken();
        Long id = userSysForm.getId();
        UserSys userSys = null;
        if (StringUtils.isNotBlank(token)) {
            userSys = userSysRepository.findUserByToken(token);
        } else {
            userSys = userSysRepository.findOne(id);
        }
        if (userSys.getOwner() == 2) {
            userSys.setOwner(userSysForm.getOwner());
            if (userSysForm.getOwner() == 1) {
                // 发送短信
                smsHelper.sendSms(userSys.getMobile(), smsHelper.smsProperties.getTplOwnerOk());
            } else if (userSysForm.getOwner() == 3) {
                // 发送短信
                smsHelper.sendSms(userSys.getMobile(), smsHelper.smsProperties.getTplOwnerFail());
            }
        } else if (userSys.getOwner() == 1) {
            resData.setCode(1).setMsg("已经是车主");
        } else {
            resData.setCode(2).setMsg("用户未提交审核");
        }
        return resData;
    }

    /**
     * 用户列表
     */
    public ResData list(UserSysForm userSysForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(userSysForm);
        Page<UserSys> pageUserSys = userSysRepository.findAll(new Specification<UserSys>() {
            @Override
            public Predicate toPredicate(Root<UserSys> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                if (StringUtils.isNotBlank(userSysForm.getNickname())) {
                    expressions.add(cb.like(root.get("nickname"), "%" + userSysForm.getNickname() + "%"));
                }
                if (userSysForm.getOwner() != null) {
                    expressions.add(cb.equal(root.get("owner"), userSysForm.getOwner()));
                }
                if (userSysForm.getStatus() != null) {
                    expressions.add(cb.equal(root.get("status"), userSysForm.getStatus()));
                }
                return predicate;
            }
        }, pageable);
        resData.putAll(PageUtils.toMap("userList", pageUserSys));
        return resData;
    }

}
