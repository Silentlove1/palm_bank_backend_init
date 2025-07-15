package com.life.bank.palm.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.life.bank.palm"})
@MapperScan(basePackages = {"com.life.bank.palm.dao"})
@EnableScheduling
public class PalmBankLifeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PalmBankLifeApplication.class, args);
    }
}