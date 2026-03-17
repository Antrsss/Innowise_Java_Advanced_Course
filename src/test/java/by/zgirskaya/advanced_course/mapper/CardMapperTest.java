package by.zgirskaya.advanced_course.mapper;

import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CardMapperTest {

  @Autowired
  private CardMapper cardMapper;

  private PaymentCard paymentCard;
  private PaymentCardDto paymentCardDto;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("John");
    user.setSurname("Doe");
    user.setEmail("john.doe@example.com");
    user.setActive(true);

    paymentCard = new PaymentCard();
    paymentCard.setId(10L);
    paymentCard.setNumber("1234567890123456");
    paymentCard.setHolder("John Doe");
    paymentCard.setExpirationDate("12/25");
    paymentCard.setActive(true);
    paymentCard.setUser(user);

    paymentCardDto = new PaymentCardDto();
    paymentCardDto.setId(10L);
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setExpirationDate("12/25");
    paymentCardDto.setActive(true);
    paymentCardDto.setUserId(1L);
  }

  @Test
  void testToDto_ShouldMapAllFields() {
    PaymentCardDto result = cardMapper.toDto(paymentCard);

    assertNotNull(result, "Mapped DTO should not be null");
    assertEquals(paymentCard.getId(), result.getId());
    assertEquals(paymentCard.getNumber(), result.getNumber());
    assertEquals(paymentCard.getHolder(), result.getHolder());
    assertEquals(paymentCard.getExpirationDate(), result.getExpirationDate());
    assertEquals(paymentCard.isActive(), result.isActive());
    assertEquals(paymentCard.getUser().getId(), result.getUserId());
  }

  @Test
  void testToDto_WithNullUser_ShouldMapWithoutUser() {
    paymentCard.setUser(null);

    PaymentCardDto result = cardMapper.toDto(paymentCard);

    assertNotNull(result, "Mapped DTO should not be null");
    assertEquals(paymentCard.getId(), result.getId());
    assertEquals(paymentCard.getNumber(), result.getNumber());
    assertEquals(paymentCard.getHolder(), result.getHolder());
    assertEquals(paymentCard.getExpirationDate(), result.getExpirationDate());
    assertEquals(paymentCard.isActive(), result.isActive());
    assertNull(result.getUserId(), "UserId should be null when user is null");
  }

  @Test
  void testToDto_WithNullPaymentCard_ShouldReturnNull() {
    PaymentCardDto result = cardMapper.toDto(null);

    assertNull(result, "Mapping null should return null");
  }

  @Test
  void testToEntity_ShouldMapAllFields() {
    PaymentCard result = cardMapper.toEntity(paymentCardDto);

    assertNotNull(result, "Mapped entity should not be null");
    assertEquals(paymentCardDto.getId(), result.getId());
    assertEquals(paymentCardDto.getNumber(), result.getNumber());
    assertEquals(paymentCardDto.getHolder(), result.getHolder());
    assertEquals(paymentCardDto.getExpirationDate(), result.getExpirationDate());
    assertEquals(paymentCardDto.isActive(), result.isActive());

    assertNotNull(result.getUser(), "User should not be null");
    assertEquals(paymentCardDto.getUserId(), result.getUser().getId());

    assertNull(result.getUser().getName());
    assertNull(result.getUser().getSurname());
    assertNull(result.getUser().getEmail());
  }

  @Test
  void testToEntity_WithNullPaymentCardDto_ShouldReturnNull() {
    PaymentCard result = cardMapper.toEntity(null);
    assertNull(result, "Mapping null should return null");
  }

  @Test
  void testBidirectionalMapping_ShouldBeConsistent() {
    PaymentCard originalEntity = paymentCard;

    PaymentCardDto dto = cardMapper.toDto(originalEntity);
    PaymentCard mappedEntity = cardMapper.toEntity(dto);

    assertNotNull(mappedEntity);
    assertEquals(originalEntity.getId(), mappedEntity.getId());
    assertEquals(originalEntity.getNumber(), mappedEntity.getNumber());
    assertEquals(originalEntity.getHolder(), mappedEntity.getHolder());
    assertEquals(originalEntity.getExpirationDate(), mappedEntity.getExpirationDate());
    assertEquals(originalEntity.isActive(), mappedEntity.isActive());

    assertEquals(originalEntity.getUser().getId(), mappedEntity.getUser().getId());
  }

  @Test
  void testToDto_WithPartialData_ShouldMapAvailableFields() {
    PaymentCard partialCard = new PaymentCard();
    partialCard.setId(20L);
    partialCard.setNumber("9876543210987654");

    PaymentCardDto result = cardMapper.toDto(partialCard);

    assertNotNull(result);
    assertEquals(partialCard.getId(), result.getId());
    assertEquals(partialCard.getNumber(), result.getNumber());
    assertNull(result.getHolder());
    assertNull(result.getExpirationDate());
    assertFalse(result.isActive());
    assertNull(result.getUserId());
  }

  @Test
  void testMapUser_WithValidId_ShouldReturnUser() {
    Long userId = 99L;

    User result = cardMapper.mapUser(userId);

    assertNotNull(result);
    assertEquals(userId, result.getId());
    assertNull(result.getName());
    assertNull(result.getSurname());
    assertNull(result.getEmail());
  }

  @Test
  void testMapUser_WithNullId_ShouldReturnNull() {
    User result = cardMapper.mapUser(null);
    assertNull(result);
  }

  @Test
  void testToEntity_WithZeroUserId_ShouldMapWithUser() {
    paymentCardDto.setUserId(0L);

    PaymentCard result = cardMapper.toEntity(paymentCardDto);

    assertNotNull(result);
    assertNotNull(result.getUser());
    assertEquals(0L, result.getUser().getId());
  }

  @Test
  void testToEntity_WithNegativeUserId_ShouldMapWithUser() {
    paymentCardDto.setUserId(-5L);

    PaymentCard result = cardMapper.toEntity(paymentCardDto);

    assertNotNull(result);
    assertNotNull(result.getUser());
    assertEquals(-5L, result.getUser().getId());
  }

  @Test
  void testToDto_WithUserHavingOnlyId_ShouldMapUserId() {
    User userWithOnlyId = new User();
    userWithOnlyId.setId(42L);

    PaymentCard card = new PaymentCard();
    card.setId(100L);
    card.setNumber("1111222233334444");
    card.setHolder("Test Holder");
    card.setExpirationDate("12/30");
    card.setActive(true);
    card.setUser(userWithOnlyId);

    PaymentCardDto result = cardMapper.toDto(card);

    assertNotNull(result);
    assertEquals(42L, result.getUserId());
    assertEquals("Test Holder", result.getHolder());
  }

  @Test
  void testToEntity_WithEmptyStringFields_ShouldMapEmptyStrings() {
    paymentCardDto.setNumber("");
    paymentCardDto.setHolder("");
    paymentCardDto.setExpirationDate("");

    PaymentCard result = cardMapper.toEntity(paymentCardDto);

    assertNotNull(result);
    assertEquals("", result.getNumber());
    assertEquals("", result.getHolder());
    assertEquals("", result.getExpirationDate());
    assertNotNull(result.getUser());
    assertEquals(1L, result.getUser().getId());
  }

  @Test
  void testToDto_WithEmptyStringFields_ShouldMapEmptyStrings() {
    paymentCard.setNumber("");
    paymentCard.setHolder("");
    paymentCard.setExpirationDate("");

    PaymentCardDto result = cardMapper.toDto(paymentCard);

    assertNotNull(result);
    assertEquals("", result.getNumber());
    assertEquals("", result.getHolder());
    assertEquals("", result.getExpirationDate());
    assertEquals(1L, result.getUserId());
  }

  @Test
  void testToEntity_WithSpecialCharacters_ShouldMapCorrectly() {
    paymentCardDto.setNumber("1234-5678-9012-3456");
    paymentCardDto.setHolder("O'Connor Smith-Johnson");
    paymentCardDto.setExpirationDate("12/25");

    PaymentCard result = cardMapper.toEntity(paymentCardDto);

    assertNotNull(result);
    assertEquals("1234-5678-9012-3456", result.getNumber());
    assertEquals("O'Connor Smith-Johnson", result.getHolder());
    assertEquals("12/25", result.getExpirationDate());
  }

  @Test
  void testMapUser_AlwaysReturnsNewInstance() {
    Long userId = 1L;

    User user1 = cardMapper.mapUser(userId);
    User user2 = cardMapper.mapUser(userId);

    assertNotSame(user1, user2, "Should return different instances");
    assertEquals(user1.getId(), user2.getId());
  }

  @Test
  void testToEntity_DoesNotModifyOriginalDto() {
    PaymentCardDto originalDto = new PaymentCardDto();
    originalDto.setId(10L);
    originalDto.setNumber("1234567890123456");
    originalDto.setHolder("John Doe");
    originalDto.setUserId(1L);

    PaymentCardDto copyDto = new PaymentCardDto();
    copyDto.setId(originalDto.getId());
    copyDto.setNumber(originalDto.getNumber());
    copyDto.setHolder(originalDto.getHolder());
    copyDto.setUserId(originalDto.getUserId());

    cardMapper.toEntity(originalDto);

    assertEquals(copyDto.getId(), originalDto.getId());
    assertEquals(copyDto.getNumber(), originalDto.getNumber());
    assertEquals(copyDto.getHolder(), originalDto.getHolder());
    assertEquals(copyDto.getUserId(), originalDto.getUserId());
  }
}