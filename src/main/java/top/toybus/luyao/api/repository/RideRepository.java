package top.toybus.luyao.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.common.repository.BaseRepository;

public interface RideRepository extends BaseRepository<Ride, Long> {

	/**
	 * 查询车次列表和级联乘客列表
	 */
	Page<Ride> findAllByTemplateFalse(Pageable pageable);

	/**
	 * 查询指定车主的车次模板
	 */
	Page<Ride> findAllByTemplateTrueAndUser(User user, Pageable pageable);

}
