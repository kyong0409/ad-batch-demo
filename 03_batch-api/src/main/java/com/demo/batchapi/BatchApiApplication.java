package com.demo.batchapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan({
    "com.demo.batchapi.batch.history.mapper",
    "com.demo.batchapi.batch.schedule.mapper"
})
@SpringBootApplication(exclude = {
    org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class
})
public class BatchApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchApiApplication.class, args);
    }
}
