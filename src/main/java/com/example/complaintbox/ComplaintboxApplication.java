package com.example.complaintbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ComplaintboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ComplaintboxApplication.class,
                args
        );
    }
}
