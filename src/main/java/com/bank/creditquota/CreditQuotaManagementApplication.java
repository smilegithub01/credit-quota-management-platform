package com.bank.creditquota;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bank.creditquota.mapper")
public class CreditQuotaManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditQuotaManagementApplication.class, args);
    }

}