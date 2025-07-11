package com.formation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
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

}
