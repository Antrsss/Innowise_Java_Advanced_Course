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
}