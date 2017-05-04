package top.toybus.luyao.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import top.toybus.luyao.api.entity.User;
import top.toybus.luyao.common.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {

    /**
     * 根据手机号获得用户信息
     */
    User findFirstByMobile(String mobile);

    @EntityGraph(attributePaths = "carList")
    Page<User> findAllBy_(Pageable pageable);

}
