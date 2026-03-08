package com.github.soillux;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@Slf4j
@SpringBootApplication
public class SoilluxApplication implements ApplicationRunner {

  public static void main(String[] args) {
    SpringApplication.run(SoilluxApplication.class, args);
  }

  @Override
  public void run(@NonNull ApplicationArguments args) throws SQLException {
    log.info("Started Soillux Valley");
    log.info("http://localhost:8080/api/swagger-ui.html");
    log.info("http://localhost:8080/api/v3/api-docs");

  }
}
