package top.toybus.luyao.sys.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.toybus.luyao.common.bean.ResData;
import top.toybus.luyao.common.helper.SmsHelper;
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
     * 认证车主成功
     */
    public ResData verifyOwnerOk(UserSysForm userSysForm) {
        ResData resData = ResData.get();
        String token = userSysForm.getToken();
        Long id = userSysForm.getId();
        UserSys userSys = null;
        if (StringUtils.isNotBlank(token)) {
            userSys = userSysRepository.findUserByToken(token);
        } else {
            userSys = userSysRepository.findOne(id);
        }
        if (userSys.getOwner() != null && !userSys.getOwner()) {
            userSys.setOwner(true);
            // 发送短信
            smsHelper.sendSms(userSys.getMobile(), smsHelper.smsProperties.getTplOwnerOk());
        } else {
            resData.setCode(1).setMsg("已经是车主");
        }
        return resData;
    }

}
