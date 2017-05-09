package top.toybus.luyao.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * spring jpa 配置
 * 
 * @author sunxg
 */
@Configuration
@EnableJpaRepositories(basePackages = "top.toybus.luyao.**.repository", enableDefaultTransactions = false) // 使用service层的事务
@EntityScan("top.toybus.luyao.**.entity")
public class SpringJpaConfig {

	@Primary
	@Bean
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	/**
	 * 使用druid数据源 <a href="https://github.com/alibaba/druid/wiki">参考文档</a>
	 */
	@Primary
	@Bean(initMethod = "init", destroyMethod = "close")
	@ConfigurationProperties("spring.datasource.druid")
	public DruidDataSource druidDataSource() {
		DruidDataSource druidDataSource = (DruidDataSource) dataSourceProperties().initializeDataSourceBuilder()
				.type(DruidDataSource.class).build();
		return druidDataSource;
	}

}
