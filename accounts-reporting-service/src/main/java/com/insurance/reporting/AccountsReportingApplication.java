package com.insurance.reporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AccountsReportingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsReportingApplication.class, args);
    }
}
