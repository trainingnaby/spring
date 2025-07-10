package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public DataInitializer(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        AppUser user = new AppUser();
        user.setUsername("dbuser");
        user.setPassword(encoder.encode("password"));
        user.setRole("USER");
        repo.save(user);
    }
}
