package top.toybus.luyao.api.repository;

import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.common.repository.BaseRepository;

public interface UserRideRepository extends BaseRepository<UserRide, Long> {

    UserRide findByUserIdAndRide(Long userId, Ride ride);

    UserRide findByOrderNo(Long orderNo);

}
