package com.alexbezsh.microservices.creditbureau;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Credit Bureau"))
@SpringBootApplication(scanBasePackages = "com.alexbezsh.microservices")
public class CreditBureauApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditBureauApplication.class, args);
    }

}
