package com.pawever.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling //스케줄링 활성화
public class PawEverApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawEverApplication.class, args);
    }

}
