package com.formation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.formation")
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = true)
@PropertySource(value = {"classpath:application-${spring.profiles.active}.properties"}, ignoreResourceNotFound = true)
public class AppConfig {

}
