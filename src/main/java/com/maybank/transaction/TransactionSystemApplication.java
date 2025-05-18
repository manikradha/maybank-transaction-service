package com.maybank.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.maybank.transaction.repository")
@EntityScan(basePackages = "com.maybank.transaction.entity")
public class TransactionSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionSystemApplication.class, args);
    }
}