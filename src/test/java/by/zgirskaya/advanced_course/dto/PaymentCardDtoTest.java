package by.zgirskaya.advanced_course.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCardDtoTest {

  private Validator validator;
  private PaymentCardDto paymentCardDto;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }

    paymentCardDto = new PaymentCardDto();
  }

  @Test
  void testGettersAndSetters() {
    Long id = 1L;
    String number = "1234567890123456";
    String holder = "John Doe";
    String expirationDate = "12/25";
    boolean active = true;
    Long userId = 100L;

    paymentCardDto.setId(id);
    paymentCardDto.setNumber(number);
    paymentCardDto.setHolder(holder);
    paymentCardDto.setExpirationDate(expirationDate);
    paymentCardDto.setActive(active);
    paymentCardDto.setUserId(userId);

    assertEquals(id, paymentCardDto.getId());
    assertEquals(number, paymentCardDto.getNumber());
    assertEquals(holder, paymentCardDto.getHolder());
    assertEquals(expirationDate, paymentCardDto.getExpirationDate());
    assertEquals(active, paymentCardDto.isActive());
    assertEquals(userId, paymentCardDto.getUserId());
  }

  @Test
  void testValidPaymentCard() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setExpirationDate("12/25");
    paymentCardDto.setActive(true);
    paymentCardDto.setUserId(1L);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Expected no validation errors for valid card");
  }

  @Test
  void testInvalidCardNumber_TooShort() {
    paymentCardDto.setNumber("1234");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<PaymentCardDto> violation = violations.iterator().next();
    assertEquals("number", violation.getPropertyPath().toString());
    assertEquals("Card number must be 16 digits", violation.getMessage());
  }

  @Test
  void testInvalidCardNumber_WithLetters() {
    paymentCardDto.setNumber("123456789012345A");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());

    ConstraintViolation<PaymentCardDto> violation = violations.iterator().next();
    assertEquals("number", violation.getPropertyPath().toString());
    assertEquals("Card number must be 16 digits", violation.getMessage());
  }

  @Test
  void testBlankHolder() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());

    boolean hasHolderViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("holder")
            && v.getMessage().equals("Holder name is required"));

    assertTrue(hasHolderViolation, "Expected validation error for blank holder");
  }

  @Test
  void testNullHolder() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder(null);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());

    boolean hasHolderViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("holder")
            && v.getMessage().equals("Holder name is required"));

    assertTrue(hasHolderViolation, "Expected validation error for null holder");
  }

  @Test
  void testMultipleViolations() {
    paymentCardDto.setNumber("123");
    paymentCardDto.setHolder("");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertEquals(2, violations.size(), "Expected two validation violations");
  }

  @Test
  void testEqualsAndHashCode() {
    PaymentCardDto card1 = new PaymentCardDto();
    card1.setId(1L);
    card1.setNumber("1234567890123456");

    PaymentCardDto card2 = new PaymentCardDto();
    card2.setId(1L);
    card2.setNumber("1234567890123456");

    PaymentCardDto card3 = new PaymentCardDto();
    card3.setId(2L);
    card3.setNumber("9999999999999999");

    assertEquals(card1, card2, "Cards with same data should be equal");
    assertEquals(card1.hashCode(), card2.hashCode(), "HashCodes should be equal for equal objects");
    assertNotEquals(card1, card3, "Cards with different data should not be equal");
  }

  @Test
  void testToString() {
    paymentCardDto.setId(1L);
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setActive(true);

    String toString = paymentCardDto.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("holder=John Doe"));
    assertTrue(toString.contains("active=true"));
  }

  @Test
  void testCanEqual() {
    PaymentCardDto card1 = new PaymentCardDto();
    PaymentCardDto card2 = new PaymentCardDto();
    Object notACard = new Object();

    assertTrue(card1.canEqual(card2), "Card should canEqual another Card");
    assertFalse(card1.canEqual(notACard), "Card should not canEqual non-Card object");
  }
}