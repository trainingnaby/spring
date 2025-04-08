package com.formation.autobean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ExempleBeanAutoConfiguree.class)
public class SetupAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExempleBeanAutoConfiguree helloService() {
        return new ExempleBeanAutoConfiguree();
    }
}
