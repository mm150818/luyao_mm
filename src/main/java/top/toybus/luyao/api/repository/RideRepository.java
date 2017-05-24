package top.toybus.luyao.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.common.repository.BaseRepository;

public interface RideRepository extends BaseRepository<Ride, Long> {

	/**
	 * 查询行程列表和级联乘客列表
	 */
	Page<Ride> findAllByTemplateFalse(Pageable pageable);

	/**
	 * 查询指定车主的行程模板
	 */
	Page<Ride> findAllByTemplateTrueAndOwner(User owner, Pageable pageable);

	/**
	 * 查询今天发布的行程数
	 */
	@Query("select count(1) from Ride r where r.owner = ?1 and template = 0 and TO_DAYS(r.createTime) = TO_DAYS(NOW())")
	long countByPubToday(User owner);

	/**
	 * 查询指定ID的行程模板是否存在
	 */
	boolean existsByTemplateTrueAndId(Long id);

	Ride findOneByIdAndOwner(Long id, User loginUser);

}
