package top.toybus.luyao.api.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.common.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {
	/**
	 * 根据手机号获得用户信息
	 * 
	 * @param mobile
	 * @return User
	 */
	User findUserByMobile(String mobile);

	/**
	 * 根据令牌获得登录用户
	 * 
	 * @param token
	 * @return User
	 */
	User findUserByToken(String token);

	/**
	 * 修改密码
	 */
	@Modifying
	@Query("update User u set u.password = ?2, u.updateTime = ?3 where u.id = ?1")
	int updateUserPwdById(Long id, String password, LocalDateTime updateTime);

	/**
	 * 修改密码
	 */
	@Modifying
	@Query("update User u set u.password = ?2, u.updateTime = ?3 where u.mobile = ?1")
	int updateUserPwdByMobile(String mobile, String password, LocalDateTime updateTime);

}
