package top.toybus.luyao.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * spring jpa 配置
 * 
 * @author sunxg
 */
@Configuration
@EnableJpaRepositories(basePackages = "top.toybus.luyao.**.repository", enableDefaultTransactions = false) // 使用service层的事务
@EntityScan("top.toybus.luyao.**.entity")
public class SpringJpaConfig {

}
