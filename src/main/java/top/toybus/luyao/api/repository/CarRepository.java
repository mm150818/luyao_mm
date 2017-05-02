package top.toybus.luyao.api.repository;

import top.toybus.luyao.api.entity.Car;
import top.toybus.luyao.common.repository.BaseRepository;

public interface CarRepository extends BaseRepository<Car, Long> {

    // @Query("select c from Car left join fetch c.users")
    // Page<Car> findAll(Pageable pageable);
}
