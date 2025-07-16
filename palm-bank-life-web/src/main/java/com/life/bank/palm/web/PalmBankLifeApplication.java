package com.life.bank.palm.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.life.bank.palm"})
@MapperScan(basePackages = {"com.life.bank.palm.dao"})  // 更具体的路径
public class PalmBankLifeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PalmBankLifeApplication.class, args);
    }
}