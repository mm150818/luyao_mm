package top.toybus.luyao.api.repository;

import org.springframework.data.jpa.repository.Query;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.common.repository.BaseRepository;

public interface SmsRepository extends BaseRepository<Sms, Long> {
    /**
     * 查询指定手机号发送的最后一条已发送的短信
     * 
     * @param mobile
     * @param status
     * @return Sms
     */
    @Query(value = "select * from tb_sms where mobile = ?1 and status = 0 order by id desc limit 1", nativeQuery = true)
    Sms findLastSendSms(String mobile);
}
