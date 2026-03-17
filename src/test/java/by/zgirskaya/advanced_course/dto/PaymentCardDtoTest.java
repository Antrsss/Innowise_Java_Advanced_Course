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

  @Test
  void testCardNumberExactly16Digits_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Card with exactly 16 digits should be valid");
  }

  @Test
  void testCardNumberWithLeadingZeros_ShouldBeValid() {
    paymentCardDto.setNumber("0000123456789012");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Card with leading zeros should be valid");
  }

  @Test
  void testCardNumberWithSpaces_ShouldBeInvalid() {
    paymentCardDto.setNumber("1234 5678 9012 3456");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());

    boolean hasNumberViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("number"));

    assertTrue(hasNumberViolation, "Card with spaces should be invalid");
  }

  @Test
  void testCardNumberWithHyphens_ShouldBeInvalid() {
    paymentCardDto.setNumber("1234-5678-9012-3456");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());

    boolean hasNumberViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("number"));

    assertTrue(hasNumberViolation, "Card with hyphens should be invalid");
  }

  @Test
  void testCardNumberWithSpecialChars_ShouldBeInvalid() {
    paymentCardDto.setNumber("1234 5678 9012 3456");
    paymentCardDto.setHolder("John Doe");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());
  }

  @Test
  void testHolderWithMinLength_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("J");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Holder with length 1 should be valid if not blank");
  }

  @Test
  void testHolderWithMaxLength_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("J".repeat(100));

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Very long holder should be valid (no max length constraint)");
  }

  @Test
  void testHolderWithSpaces_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John M. Doe Jr.");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Holder with spaces and dots should be valid");
  }

  @Test
  void testHolderWithSpecialCharacters_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("O'Connor Smith-Johnson");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Holder with apostrophe and hyphen should be valid");
  }

  @Test
  void testExpirationDate_Null_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setExpirationDate(null);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Null expiration date should be valid");
  }

  @Test
  void testExpirationDate_Empty_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setExpirationDate("");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Empty expiration date should be valid");
  }

  @Test
  void testUserId_Null_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setUserId(null);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Null userId should be valid");
  }

  @Test
  void testUserId_Zero_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setUserId(0L);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Zero userId should be valid");
  }

  @Test
  void testUserId_Negative_ShouldBeValid() {
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setUserId(-5L);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertTrue(violations.isEmpty(), "Negative userId should be valid (no validation)");
  }

  @Test
  void testActive_DefaultValue_ShouldBeFalse() {
    PaymentCardDto newCard = new PaymentCardDto();
    assertFalse(newCard.isActive(), "Default active should be false");
  }

  @Test
  void testAllFieldsNull_ShouldHaveViolations() {
    paymentCardDto.setNumber(null);
    paymentCardDto.setHolder(null);
    paymentCardDto.setExpirationDate(null);
    paymentCardDto.setUserId(null);

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertFalse(violations.isEmpty());
    assertEquals(1, violations.size());
  }

  @Test
  void testEqualsWithSameObject_ShouldBeTrue() {
    PaymentCardDto card = new PaymentCardDto();
    card.setId(1L);
    card.setNumber("1234567890123456");

    assertEquals(card, card, "Object should equal itself");
  }

  @Test
  void testEqualsWithNull_ShouldBeFalse() {
    PaymentCardDto card = new PaymentCardDto();
    card.setId(1L);

    assertNotEquals(null, card, "Card should not equal null");
  }

  @Test
  void testEqualsWithDifferentClass_ShouldBeFalse() {
    PaymentCardDto card = new PaymentCardDto();
    card.setId(1L);

    String notACard = "not a card";

    assertNotEquals(card, notACard, "Card should not equal different class");
  }

  @Test
  void testEqualsWithSameIdButDifferentNumber_ShouldBeFalse() {
    PaymentCardDto card1 = new PaymentCardDto();
    card1.setId(1L);
    card1.setNumber("1111222233334444");

    PaymentCardDto card2 = new PaymentCardDto();
    card2.setId(1L);
    card2.setNumber("5555666677778888");

    assertNotEquals(card1, card2, "Cards with same ID but different numbers should not be equal");
  }

  @Test
  void testHashCodeConsistency_ShouldBeStable() {
    PaymentCardDto card = new PaymentCardDto();
    card.setId(1L);
    card.setNumber("1234567890123456");
    card.setHolder("John Doe");

    int hashCode1 = card.hashCode();
    int hashCode2 = card.hashCode();

    assertEquals(hashCode1, hashCode2, "HashCode should be consistent");
  }

  @Test
  void testToString_WithNullFields_ShouldNotThrow() {
    paymentCardDto.setId(null);
    paymentCardDto.setNumber(null);
    paymentCardDto.setHolder(null);
    paymentCardDto.setExpirationDate(null);
    paymentCardDto.setUserId(null);

    String toString = paymentCardDto.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("id=null"));
    assertTrue(toString.contains("number=null"));
    assertTrue(toString.contains("holder=null"));
  }

  @Test
  void testBuilderStyle_IfLombokGenerated() {
    PaymentCardDto card1 = new PaymentCardDto();
    PaymentCardDto card2 = new PaymentCardDto();

    card1.setId(1L);
    card2.setId(2L);
    assertNotEquals(card1.hashCode(), card2.hashCode());

    card1.setNumber("1234567890123456");
    card1.setHolder("Test Holder");
    card1.setExpirationDate("12/25");
    card1.setActive(true);
    card1.setUserId(1L);
  }

  @Test
  void testValidationMessages_AreCorrect() {
    paymentCardDto.setNumber("123");
    paymentCardDto.setHolder("");

    Set<ConstraintViolation<PaymentCardDto>> violations = validator.validate(paymentCardDto);

    assertEquals(2, violations.size());

    for (ConstraintViolation<PaymentCardDto> violation : violations) {
      if (violation.getPropertyPath().toString().equals("number")) {
        assertEquals("Card number must be 16 digits", violation.getMessage());
      } else if (violation.getPropertyPath().toString().equals("holder")) {
        assertEquals("Holder name is required", violation.getMessage());
      }
    }
  }
}