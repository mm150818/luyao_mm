package top.toybus.luyao.api.repository;

import top.toybus.luyao.api.entity.Payment;
import top.toybus.luyao.common.repository.BaseRepository;

public interface PaymentRepository extends BaseRepository<Payment, Long> {

    Payment findByOrderNo(Long orderNo);

}
