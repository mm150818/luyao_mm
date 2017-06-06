package top.toybus.luyao.api.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public interface UserBean {
    String getNickname();

    String getMobile();

    Integer getStatus();

    @Value("#{target.occupation}")
    List<String> getRideList();
}
