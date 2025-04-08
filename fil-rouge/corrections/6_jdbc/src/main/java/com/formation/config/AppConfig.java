package com.formation.config;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.formation")
@PropertySource(value = { "classpath:application.properties" }, ignoreResourceNotFound = true)
@EnableWebMvc
@EnableTransactionManagement
public class AppConfig {

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	// Datasource : interface entre l'application et la base de données : gére un pool de connexion réutilisable, optimsée
	// chaque éditeur de base de données fournit une classe implémentant l'interface javax.sql.DataSource
	@Bean
	public DataSource dataSource() {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:~/myFirstH2Database;INIT=RUNSCRIPT FROM 'classpath:schema.sql'");
		ds.setUser("sa");
		ds.setPassword("sa");
		return ds;
	}

	// JdbcTemplate : Fait les opérations en base de données
	@Bean
	JdbcTemplate beanJdbcTemplate() {
		// doit connaitre le composant qui lui permet d'accéder à la base
		return new JdbcTemplate(dataSource());
	}
	
	// gestionnaire des transactions
	@Bean
	public TransactionManager platformTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

}
