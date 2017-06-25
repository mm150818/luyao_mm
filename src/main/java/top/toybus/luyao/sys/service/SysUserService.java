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
import top.toybus.luyao.sys.entity.SysUser;
import top.toybus.luyao.sys.entity.SysVehicle;
import top.toybus.luyao.sys.formbean.SysUserForm;
import top.toybus.luyao.sys.repository.SysUserRepository;
import top.toybus.luyao.sys.repository.SysVehicleRepository;

@Service
@Transactional
public class SysUserService {
    @Autowired
    private SysUserRepository sysUserRepository;
    @Autowired
    private SysVehicleRepository sysVehicleRepository;
    @Autowired
    private SmsHelper smsHelper;

    /**
     * 认证车主
     */
    public ResData verifyOwner(SysUserForm userSysForm) {
        ResData resData = ResData.get();
        Long id = userSysForm.getId();
        SysUser userSys = sysUserRepository.findOne(id);
        userSys.setOwner(userSysForm.getOwner());
        if (userSysForm.getOwner() == 1) {
            // 发送短信
            smsHelper.sendSms(userSys.getMobile(), smsHelper.smsProperties.getTplOwnerOk());
        } else if (userSysForm.getOwner() == 3) {
            // 发送短信
            smsHelper.sendSms(userSys.getMobile(), smsHelper.smsProperties.getTplOwnerFail());
        }
        return resData;
    }

    /**
     * 用户列表
     */
    public ResData list(SysUserForm userSysForm) {
        ResData resData = ResData.get();
        Pageable pageable = PageUtils.toPageRequest(userSysForm);
        Page<SysUser> pageUserSys = sysUserRepository.findAll(new Specification<SysUser>() {
            @Override
            public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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

    /**
     * 详情
     */
    public ResData detail(SysUserForm userSysForm) {
        ResData resData = ResData.get();
        Long id = userSysForm.getId();
        SysUser user = sysUserRepository.findOne(id);
        if (user.getVehicleId() != null) {
            SysVehicle vehicle = sysVehicleRepository.findOne(user.getVehicleId());
            user.setVehicle(vehicle);
        }
        resData.put("user", user);
        return resData;
    }

}
