package top.toybus.luyao.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.common.repository.BaseRepository;

public interface RideRepository extends BaseRepository<Ride, Long> {
	/**
	 * 级联查询车列表和定位乘客列表
	 */
	// @EntityGraph(attributePaths = { "rideUserList.user" })
	Page<Ride> findAllBy(Pageable pageable);
}
