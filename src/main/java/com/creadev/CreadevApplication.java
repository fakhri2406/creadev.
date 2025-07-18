package com.creadev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CreadevApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreadevApplication.class, args);
    }

}
