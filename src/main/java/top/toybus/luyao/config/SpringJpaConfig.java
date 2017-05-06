package top.toybus.luyao.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * spring jpa 配置
 * 
 * @author sunxg
 */
@Configuration
@EnableJpaRepositories(basePackages = "top.toybus.luyao.**.repository", enableDefaultTransactions = false)
@PropertySource("classpath:/dbconfig.properties")
public class SpringJpaConfig {
	@Autowired
	private Environment env;

	/**
	 * 数据源配置，使用druid数据库连接池
	 */
	@Bean(initMethod = "init", destroyMethod = "close")
	public DruidDataSource druidDataSource() throws Exception {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(env.getRequiredProperty("jdbc.driverClassName"));
		druidDataSource.setUrl(env.getRequiredProperty("jdbc.url"));
		druidDataSource.setUsername(env.getRequiredProperty("jdbc.username"));
		druidDataSource.setPassword(env.getRequiredProperty("jdbc.password"));

		druidDataSource.setInitialSize(env.getRequiredProperty("druid.initialSize", int.class));
		druidDataSource.setMaxActive(env.getRequiredProperty("druid.maxActive", int.class));
		druidDataSource.setMaxWait(env.getRequiredProperty("druid.maxWait", long.class));
		druidDataSource.setMinIdle(env.getRequiredProperty("druid.minIdle", int.class));

		druidDataSource.setTimeBetweenEvictionRunsMillis(
				env.getRequiredProperty("druid.timeBetweenEvictionRunsMillis", long.class));
		druidDataSource
				.setMinEvictableIdleTimeMillis(env.getRequiredProperty("druid.minEvictableIdleTimeMillis", long.class));

		druidDataSource.setValidationQuery(env.getRequiredProperty("druid.validationQuery"));
		druidDataSource.setTestWhileIdle(env.getRequiredProperty("druid.testWhileIdle", boolean.class));
		druidDataSource.setTestOnBorrow(env.getRequiredProperty("druid.testOnBorrow", boolean.class));
		druidDataSource.setTestOnReturn(env.getRequiredProperty("druid.testOnReturn", boolean.class));

		druidDataSource
				.setPoolPreparedStatements(env.getRequiredProperty("druid.poolPreparedStatements", boolean.class));
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(
				env.getRequiredProperty("druid.maxPoolPreparedStatementPerConnectionSize", int.class));

		druidDataSource.setFilters(env.getRequiredProperty("druid.filters"));

		return druidDataSource;
	}

	/**
	 * 定义实体管理器工厂
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

		// DataSource dataSource = new
		// Log4jdbcProxyDataSource(druidDataSource()); // log4jdbc数据源包裹原始数据源
		DataSource dataSource = druidDataSource();
		entityManagerFactory.setDataSource(dataSource); // 指定数据源
		entityManagerFactory.setPersistenceProvider(new HibernatePersistenceProvider()); // 持久化供应商

		entityManagerFactory.setPackagesToScan("top.toybus.luyao.**.entity");
		Map<String, Object> jpaProperties = new HashMap<>();
		jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.query.substitutions", env.getRequiredProperty("hibernate.query.substitutions"));
		jpaProperties.put("hibernate.jdbc.fetch_size", env.getRequiredProperty("hibernate.jdbc.fetch_size"));
		jpaProperties.put("hibernate.jdbc.batch_size", env.getRequiredProperty("hibernate.jdbc.batch_size"));
		jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
		jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.use_sql_comments", env.getRequiredProperty("hibernate.use_sql_comments"));
		jpaProperties.put("hibernate.id.new_generator_mappings",
				env.getRequiredProperty("hibernate.id.new_generator_mappings", boolean.class));
		entityManagerFactory.setJpaPropertyMap(jpaProperties);

		return entityManagerFactory;
	}

	/**
	 * JPA事务管理器
	 */
	@Bean
	public JpaTransactionManager transactionManager() throws Exception {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}
}
