package com.github.soillux.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MapperConfigTest {

  @Test
  void mapStructConfig_should_beInstantiable_when_created() {
    // Given & When
    MapStructConfig config = new MapStructConfig();

    // Then
    assertThat(config).isNotNull();
  }
}
