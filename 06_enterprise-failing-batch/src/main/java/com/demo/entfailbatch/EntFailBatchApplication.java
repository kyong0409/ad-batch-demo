package com.demo.entfailbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
@EnableJdbcJobRepository
public class EntFailBatchApplication {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(EntFailBatchApplication.class, args)));
    }
}
