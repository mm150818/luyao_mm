package top.toybus.luyao.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import top.toybus.luyao.api.entity.Car;
import top.toybus.luyao.common.repository.BaseRepository;

public interface CarRepository extends BaseRepository<Car, Long> {
	/**
	 * 级联查询车列表和定位乘客列表
	 */
	// @EntityGraph(attributePaths = { "carUserList.user" })
	Page<Car> findAllBy(Pageable pageable);
}
