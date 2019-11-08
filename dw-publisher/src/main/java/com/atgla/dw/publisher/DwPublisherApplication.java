package com.atgla.dw.publisher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.atgla.dw.publisher.mapper")
public class DwPublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(DwPublisherApplication.class, args);
    }

}
