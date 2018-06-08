package com.assessment.access;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class DBProvider {

	/*@Bean
	public Example2 ex2() {
		return new Example2();
	}*/
	
	@Bean
	public EntityManager entityManager(@Qualifier("Assessment") EntityManagerFactory emf) {
		return emf.createEntityManager();
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager trxMgr(@Qualifier("Assessment") EntityManagerFactory emf) {
		JpaTransactionManager trxMgr = new JpaTransactionManager();
		trxMgr.setEntityManagerFactory(emf);
		return trxMgr;
	}

	@Bean(name = "Assessment")
	public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(
			@Qualifier("dataSource") DataSource ds) {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName("Assessment");
		bean.setDataSource(ds);
		bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties prop = new Properties();
		prop.put("hibernate.listeners.envers.autoRegister", "false");
		bean.setJpaProperties(prop);
		return bean;
	}

	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/assessment");
		dataSource.setUsername("root");
		dataSource.setPassword("");
		return dataSource;
	}
}
