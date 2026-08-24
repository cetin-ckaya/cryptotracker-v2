package com.cryptotracker.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// @EnableJpaAuditing: Spring'e "JPA Auditing mekanizmasını devreye al" der.
// Bu olmadan @CreatedDate/@LastModifiedDate anotasyonları TAMAMEN
// göz ardı edilir, alanlar hep null kalır.
@EnableJpaAuditing
//Spring'e "cache mekanizmasını aktif et" demek için ana sınıfa bu anotasyon eklenir
@EnableCaching
public class BackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(BackendApplication.class, args);

    }

}
