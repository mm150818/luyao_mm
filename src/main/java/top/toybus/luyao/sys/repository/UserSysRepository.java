package top.toybus.luyao.sys.repository;

import top.toybus.luyao.common.repository.BaseRepository;
import top.toybus.luyao.sys.entity.UserSys;

public interface UserSysRepository extends BaseRepository<UserSys, Long> {

    UserSys findUserByToken(String token);

}
