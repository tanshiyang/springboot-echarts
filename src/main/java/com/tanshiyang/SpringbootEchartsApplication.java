package com.tanshiyang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.tanshiyang.rps.mapper"})
public class SpringbootEchartsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootEchartsApplication.class, args);
    }

}
