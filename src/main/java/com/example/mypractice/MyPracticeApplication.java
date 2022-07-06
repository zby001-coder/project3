package com.example.mypractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MyPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPracticeApplication.class, args);
    }

}
