package com.bdu.dishmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DishmindApplication {

    public static void main(String[] args) {
        SpringApplication.run(DishmindApplication.class, args);
    }

}
