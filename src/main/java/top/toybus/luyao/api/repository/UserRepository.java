package top.toybus.luyao.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import top.toybus.luyao.api.bean.UserBean;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.common.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {

    boolean existsUserByMobile(String mobile);

    User findUserByMobile(String mobile);

    User findUserByToken(String token);

    @Modifying
    @Query("update User u set u.password = ?2, u.updateTime = ?3, u.status = 0 where u.id = ?1")
    int updateUserPwdById(Long id, String password, LocalDateTime updateTime);

    @Modifying
    @Query("update User u set u.owner = ?2, u.vehicleId = ?3 where u.id = ?1")
    int updateUserOwnerById(Long id, Boolean owner, Long vehicleId);

    List<UserBean> findListBy(Pageable pageable);

}
