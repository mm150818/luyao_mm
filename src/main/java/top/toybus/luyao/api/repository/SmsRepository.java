package top.toybus.luyao.api.repository;

import top.toybus.luyao.api.entity.Sms;
import top.toybus.luyao.common.repository.BaseRepository;

public interface SmsRepository extends BaseRepository<Sms, Long> {
	/**
	 * 查询指定手机号发送的最后一条已发送的短信
	 */
	Sms findFirstByMobileAndStatusOrderByIdDesc(String mobile, Integer status);
}
