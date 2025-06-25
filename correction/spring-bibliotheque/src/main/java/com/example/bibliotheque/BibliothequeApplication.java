package com.example.bibliotheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.bibliotheque.jpa.repository")
@EnableElasticsearchRepositories(basePackages = "com.example.bibliotheque.elasticsearch.repository")
public class BibliothequeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BibliothequeApplication.class, args);
    }
}
