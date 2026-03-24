package com.innowise.userservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Full coverage for UserDto equals, hashCode and branches")
  void testUserDtoFullCoverage() {
    UserDto dto1 = new UserDto();
    dto1.setId(1L);
    dto1.setName("John");
    dto1.setSurname("Doe");
    dto1.setEmail("john@example.com");
    dto1.setBirthDate(LocalDate.of(1990, 1, 1));
    dto1.setActive(true);
    dto1.setCards(Collections.emptyList());

    UserDto dto2 = new UserDto();
    dto2.setId(1L);
    dto2.setName("John");
    dto2.setSurname("Doe");
    dto2.setEmail("john@example.com");
    dto2.setBirthDate(LocalDate.of(1990, 1, 1));
    dto2.setActive(true);
    dto2.setCards(Collections.emptyList());

    assertThat(dto1)
        .isEqualTo(dto2)
        .isNotNull()
        .isNotEqualTo(new Object());

    assertThat(dto1.hashCode()).hasSameHashCodeAs(dto2.hashCode());

    dto2.setId(null);
    assertThat(dto1).isNotEqualTo(dto2);
    dto1.setId(null);
    assertThat(dto1).isEqualTo(dto2);
    dto1.setId(1L);
    dto2.setId(1L);

    dto2.setBirthDate(null);
    assertThat(dto1).isNotEqualTo(dto2);
    dto1.setBirthDate(null);
    assertThat(dto1).isEqualTo(dto2);
    dto1.setBirthDate(LocalDate.of(1990, 1, 1));
    dto2.setBirthDate(LocalDate.of(1990, 1, 1));

    dto2.setCards(null);
    assertThat(dto1).isNotEqualTo(dto2);
    dto1.setCards(null);
    assertThat(dto1).isEqualTo(dto2);

    assertThat(new UserDto().hashCode()).isNotEqualTo(dto1.hashCode());
    assertThat(dto1.toString()).contains("John");
    assertThat(dto1.canEqual(new UserDto())).isTrue();
  }

  @Test
  @DisplayName("Should fail validation for mandatory fields")
  void testMandatoryFields() {
    UserDto dto = new UserDto();
    Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
    assertThat(violations).isNotEmpty();
  }
}