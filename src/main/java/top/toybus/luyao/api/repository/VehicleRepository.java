package top.toybus.luyao.api.repository;

import top.toybus.luyao.api.entity.Vehicle;
import top.toybus.luyao.common.repository.BaseRepository;

public interface VehicleRepository extends BaseRepository<Vehicle, Long> {

    Vehicle findFirstByOrderByCreateTimeDesc();

}
