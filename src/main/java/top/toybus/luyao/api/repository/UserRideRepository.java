package top.toybus.luyao.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.api.entity.Ride;
import top.toybus.luyao.api.entity.UserRide;
import top.toybus.luyao.common.repository.BaseRepository;

public interface UserRideRepository extends BaseRepository<UserRide, Long> {

    UserRide findFirstByUserIdAndRideOrderByIdDesc(Long userId, Ride ride);

    UserRide findByPayment(Payment payment);

    Page<UserRide> findAllByConfirmedFalseAndPayment_StatusIs(Integer paymentStatus, Pageable pageable);

}
