package com.github.soillux;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class SoilluxApplicationTest {

  @Autowired
  private ApplicationContext context;

  @Test
  void should_haveContext_when_call() {
    // Given & When & Then
    assertNotNull(context);
  }

  @Test
  void should_runMainMethod_when_start() {
    // Given
    String[] args = new String[]{};

    // When & Then
    assertDoesNotThrow(() -> SoilluxApplication.main(args));
  }
}

