package com.linghang.wusthelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WusthelperApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");// 部署到linux上
        SpringApplication.run(WusthelperApplication.class, args);
    }

}
