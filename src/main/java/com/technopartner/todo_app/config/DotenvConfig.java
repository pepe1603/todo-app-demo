package com.technopartner.todo_app.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    
    @PostConstruct
    public void init() {
        Dotenv.configure()
            .ignoreIfMissing()
            .load()
            .entries()
            .forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
    }
}