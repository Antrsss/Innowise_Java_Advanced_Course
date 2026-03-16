package by.zgirskaya.advanced_course.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

  private Validator validator;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }

    userDto = new UserDto();
  }

  @Test
  void testGettersAndSetters() {
    Long id = 1L;
    String name = "John";
    String surname = "Doe";
    String email = "john.doe@example.com";
    boolean active = true;
    List<PaymentCardDto> cards = new ArrayList<>();
    PaymentCardDto card = new PaymentCardDto();
    card.setId(1L);
    cards.add(card);

    userDto.setId(id);
    userDto.setName(name);
    userDto.setSurname(surname);
    userDto.setEmail(email);
    userDto.setActive(active);
    userDto.setCards(cards);

    assertEquals(id, userDto.getId());
    assertEquals(name, userDto.getName());
    assertEquals(surname, userDto.getSurname());
    assertEquals(email, userDto.getEmail());
    assertEquals(active, userDto.isActive());
    assertEquals(cards, userDto.getCards());
    assertEquals(1, userDto.getCards().size());
  }

  @Test
  void testValidUser() {
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setActive(true);

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertTrue(violations.isEmpty(), "Expected no validation errors for valid user");
  }

  @Test
  void testBlankName() {
    userDto.setName("");
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasNameViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("name")
            && v.getMessage().contains("First name is required"));

    assertTrue(hasNameViolation, "Expected validation error for blank name");
  }

  @Test
  void testNullName() {
    userDto.setName(null);
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasNameViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("name")
            && v.getMessage().contains("First name is required"));

    assertTrue(hasNameViolation, "Expected validation error for null name");
  }

  @Test
  void testNameTooShort() {
    userDto.setName("J");
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasSizeViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

    assertTrue(hasSizeViolation, "Expected size validation error for too short name");
  }

  @Test
  void testNameTooLong() {
    userDto.setName("J".repeat(51));
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasSizeViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("name"));

    assertTrue(hasSizeViolation, "Expected size validation error for too long name");
  }

  @Test
  void testBlankSurname() {
    userDto.setName("John");
    userDto.setSurname("");
    userDto.setEmail("john.doe@example.com");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasSurnameViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("surname")
            && v.getMessage().contains("Surname is required"));

    assertTrue(hasSurnameViolation, "Expected validation error for blank surname");
  }

  @Test
  void testNullSurname() {
    userDto.setName("John");
    userDto.setSurname(null);
    userDto.setEmail("john.doe@example.com");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasSurnameViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("surname")
            && v.getMessage().contains("Surname is required"));

    assertTrue(hasSurnameViolation, "Expected validation error for null surname");
  }

  @Test
  void testInvalidEmail() {
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("not-an-email");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertFalse(violations.isEmpty());

    boolean hasEmailViolation = violations.stream()
        .anyMatch(v -> v.getPropertyPath().toString().equals("email")
            && v.getMessage().contains("Invalid email format"));

    assertTrue(hasEmailViolation, "Expected validation error for invalid email");
  }

  @Test
  void testBlankEmailIsValid() {
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertTrue(violations.isEmpty(), "Blank email should be valid according to @Email annotation");
  }

  @Test
  void testUserWithCards() {
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");

    List<PaymentCardDto> cards = new ArrayList<>();

    PaymentCardDto card1 = new PaymentCardDto();
    card1.setId(1L);
    card1.setNumber("1234567890123456");
    card1.setHolder("John Doe");
    cards.add(card1);

    PaymentCardDto card2 = new PaymentCardDto();
    card2.setId(2L);
    card2.setNumber("9876543210987654");
    card2.setHolder("John Doe");
    cards.add(card2);

    userDto.setCards(cards);

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertTrue(violations.isEmpty(), "User with cards should be valid");
    assertEquals(2, userDto.getCards().size());
  }

  @Test
  void testMultipleViolations() {
    userDto.setName("");
    userDto.setSurname("");
    userDto.setEmail("invalid");

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertEquals(4, violations.size(), "Expected four validation violations");
  }

  @Test
  void testEqualsAndHashCode() {
    UserDto user1 = new UserDto();
    user1.setId(1L);
    user1.setName("John");
    user1.setEmail("john@example.com");

    UserDto user2 = new UserDto();
    user2.setId(1L);
    user2.setName("John");
    user2.setEmail("john@example.com");

    UserDto user3 = new UserDto();
    user3.setId(2L);
    user3.setName("Jane");
    user3.setEmail("jane@example.com");

    assertEquals(user1, user2, "Users with same data should be equal");
    assertEquals(user1.hashCode(), user2.hashCode(), "HashCodes should be equal for equal objects");
    assertNotEquals(user1, user3, "Users with different data should not be equal");
  }

  @Test
  void testToString() {
    userDto.setId(1L);
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("john@example.com");
    userDto.setActive(true);

    String toString = userDto.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("id=1"));
    assertTrue(toString.contains("name=John"));
    assertTrue(toString.contains("surname=Doe"));
    assertTrue(toString.contains("email=john@example.com"));
    assertTrue(toString.contains("active=true"));
  }

  @Test
  void testCanEqual() {
    UserDto user1 = new UserDto();
    UserDto user2 = new UserDto();
    Object notAUser = new Object();

    assertTrue(user1.canEqual(user2), "User should canEqual another User");
    assertFalse(user1.canEqual(notAUser), "User should not canEqual non-User object");
  }
}