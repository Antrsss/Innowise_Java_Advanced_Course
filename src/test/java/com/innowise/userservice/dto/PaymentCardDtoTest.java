package com.innowise.userservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCardDtoTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Full coverage for PaymentCardDto equals, hashCode and branches")
  void testPaymentCardDtoFullCoverage() {
    PaymentCardDto dto1 = new PaymentCardDto();
    dto1.setId(1L);
    dto1.setNumber("1111222233334444");
    dto1.setHolder("IVAN IVANOV");
    dto1.setExpirationDate("12/28");
    dto1.setActive(true);
    dto1.setUserId(10L);

    PaymentCardDto dto2 = new PaymentCardDto();
    dto2.setId(1L);
    dto2.setNumber("1111222233334444");
    dto2.setHolder("IVAN IVANOV");
    dto2.setExpirationDate("12/28");
    dto2.setActive(true);
    dto2.setUserId(10L);

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

    dto2.setNumber(null);
    assertThat(dto1).isNotEqualTo(dto2);
    dto1.setNumber(null);
    assertThat(dto1).isEqualTo(dto2);
    dto1.setNumber("1111222233334444");
    dto2.setNumber("1111222233334444");

    dto2.setUserId(null);
    assertThat(dto1).isNotEqualTo(dto2);
    dto1.setUserId(null);
    assertThat(dto1).isEqualTo(dto2);

    assertThat(dto1.toString()).contains("IVAN IVANOV");
    assertThat(new PaymentCardDto().hashCode()).isNotEqualTo(dto1.hashCode());
    assertThat(dto1.canEqual(new PaymentCardDto())).isTrue();
  }

  @Test
  @DisplayName("Should fail validation for invalid formats")
  void testInvalidFormats() {
    PaymentCardDto dto = new PaymentCardDto();
    dto.setNumber("not-a-number");
    dto.setHolder("ivan");
    dto.setExpirationDate("2024/12");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(3);
  }
}