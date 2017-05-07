package top.toybus.luyao.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.api.entity.UserRide;
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
	 * 查询用户的订车列表
	 * 
	 * @param user
	 * @return List<UserRide>
	 */
	@Query("select u.userRideList from User u where u = ?1")
	List<UserRide> findUserRideListByUser(User user);
}
