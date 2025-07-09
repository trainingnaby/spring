package com.formation.config;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
@ComponentScan("com.formation")
@PropertySource(value = { "classpath:application.properties" }, ignoreResourceNotFound = true)
@PropertySource(value = { "classpath:application-${spring.profiles.active}.properties" }, ignoreResourceNotFound = true)
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class AppConfig {

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

	// permet de rechercher une vue/template associé à la chaine de caractéres
	// retournée
	// par le controlleur
	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());

		viewResolver.setOrder(1); // optionel
		viewResolver.setViewNames(new String[] { "*.html", "*.xhtml" }); // optionel
		return viewResolver;
	}

	// nécessaire au bean ThymeleafViewResolver, permet de régler plus finement
	// l'integration Spring et Thymeleaf
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		return templateEngine;
	}

	// permet de spécifier le chemin d'accés des templates
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setCacheable(false);
		return templateResolver;
	}

	@Bean
	public DataSource dataSource() {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:~/myFirstH2Database;INIT=RUNSCRIPT FROM 'classpath:schema.sql'");
		ds.setUser("sa");
		ds.setPassword("sa");
		return ds;
	}

	@Bean
	JdbcTemplate beanJdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
	public TransactionManager platformTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

}
