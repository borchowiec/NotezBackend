package com.borchowiec.notez;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NotezApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotezApplication.class, args);
    }

}
