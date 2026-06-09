package fr.formation.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = "fr.formation.aop")
@EnableAspectJAutoProxy // permet d'activer la prise en compte des aspects dans le projet
public class ConfigurationSpring {

}
