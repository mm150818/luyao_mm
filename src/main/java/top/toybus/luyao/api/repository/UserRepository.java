package top.toybus.luyao.api.repository;

import top.toybus.luyao.api.bean.UserBean;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.common.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {
	/**
	 * 根据手机号获得用户信息
	 */
	User findFirstByMobile(String mobile);

	UserBean findById(Long id);
}
