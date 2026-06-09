package fr.formation.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "fr.formation.aop")
@EnableAspectJAutoProxy
public class ConfigurationSpring {

}
