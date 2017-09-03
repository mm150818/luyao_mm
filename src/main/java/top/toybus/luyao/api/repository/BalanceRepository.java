package top.toybus.luyao.api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import top.toybus.luyao.api.entity.Activity;
import top.toybus.luyao.api.entity.Balance;
import top.toybus.luyao.common.repository.BaseRepository;

public interface BalanceRepository extends BaseRepository<Balance, Long> {

    long countByUserIdAndActivity(Long userId, Activity activity);

   /* @Modifying
	@Query("update Balance b set b.total_amount = ?2 where b.user_id = ?1")
	    int updateBalanceByUserId(Long user_id);*/
}
