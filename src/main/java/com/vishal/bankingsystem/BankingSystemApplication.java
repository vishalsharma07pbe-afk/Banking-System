package com.vishal.bankingsystem;

import com.vishal.bankingsystem.config.JwtProperties;
import com.vishal.bankingsystem.config.SecurityPolicyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan(basePackageClasses = {
        JwtProperties.class,
        SecurityPolicyProperties.class
})
public class BankingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingSystemApplication.class, args);
    }

}
