package by.zgirskaya.advanced_course.mapper;

import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.dto.UserDto;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserMapperTest {

  @Autowired
  private UserMapper userMapper;

  private User user;
  private UserDto userDto;
  private PaymentCard paymentCard;
  private PaymentCardDto paymentCardDto;

  @BeforeEach
  void setUp() {
    paymentCard = new PaymentCard();
    paymentCard.setId(10L);
    paymentCard.setNumber("1234567890123456");
    paymentCard.setHolder("John Doe");
    paymentCard.setExpirationDate("12/25");
    paymentCard.setActive(true);

    user = new User();
    user.setId(1L);
    user.setName("John");
    user.setSurname("Doe");
    user.setEmail("john.doe@example.com");
    user.setActive(true);

    paymentCard.setUser(user);
    List<PaymentCard> cards = new ArrayList<>();
    cards.add(paymentCard);
    user.setCards(cards);

    paymentCardDto = new PaymentCardDto();
    paymentCardDto.setId(10L);
    paymentCardDto.setNumber("1234567890123456");
    paymentCardDto.setHolder("John Doe");
    paymentCardDto.setExpirationDate("12/25");
    paymentCardDto.setActive(true);
    paymentCardDto.setUserId(1L);

    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setActive(true);

    List<PaymentCardDto> cardDtos = new ArrayList<>();
    cardDtos.add(paymentCardDto);
    userDto.setCards(cardDtos);
  }

  @Test
  void testToDto_WithNullCards_ShouldMapWithNullCards() {
    user.setCards(null);

    UserDto result = userMapper.toDto(user);

    assertNotNull(result, "Mapped DTO should not be null");
    assertEquals(user.getId(), result.getId());
    assertEquals(user.getName(), result.getName());
    assertEquals(user.getSurname(), result.getSurname());
    assertEquals(user.getEmail(), result.getEmail());
    assertEquals(user.isActive(), result.isActive());
    assertNull(result.getCards(), "Cards should be null when user has no cards");
  }

  @Test
  void testToDto_WithEmptyCards_ShouldMapWithEmptyList() {
    user.setCards(new ArrayList<>());

    UserDto result = userMapper.toDto(user);

    assertNotNull(result, "Mapped DTO should not be null");
    assertNotNull(result.getCards(), "Cards list should not be null");
    assertTrue(result.getCards().isEmpty(), "Cards list should be empty");
  }

  @Test
  void testToDto_WithNullUser_ShouldReturnNull() {
    UserDto result = userMapper.toDto(null);
    assertNull(result, "Mapping null should return null");
  }

  @Test
  void testToEntity_ShouldMapAllFields() {
    User result = userMapper.toEntity(userDto);

    assertNotNull(result, "Mapped entity should not be null");
    assertEquals(userDto.getId(), result.getId());
    assertEquals(userDto.getName(), result.getName());
    assertEquals(userDto.getSurname(), result.getSurname());
    assertEquals(userDto.getEmail(), result.getEmail());
    assertEquals(userDto.isActive(), result.isActive());

    assertNotNull(result.getCards(), "Cards list should not be null");
    assertEquals(1, result.getCards().size());

    PaymentCard mappedCard = result.getCards().get(0);
    assertEquals(paymentCardDto.getId(), mappedCard.getId());
    assertEquals(paymentCardDto.getNumber(), mappedCard.getNumber());
    assertEquals(paymentCardDto.getHolder(), mappedCard.getHolder());
    assertEquals(paymentCardDto.getExpirationDate(), mappedCard.getExpirationDate());
    assertEquals(paymentCardDto.isActive(), mappedCard.isActive());

    assertEquals(paymentCardDto.getUserId(), result.getId());
  }

  @Test
  void testToEntity_WithNullCards_ShouldMapWithNullCards() {
    userDto.setCards(null);

    User result = userMapper.toEntity(userDto);

    assertNotNull(result, "Mapped entity should not be null");
    assertEquals(userDto.getId(), result.getId());
    assertEquals(userDto.getName(), result.getName());
    assertEquals(userDto.getSurname(), result.getSurname());
    assertEquals(userDto.getEmail(), result.getEmail());
    assertEquals(userDto.isActive(), result.isActive());
    assertNull(result.getCards(), "Cards should be null when DTO has no cards");
  }

  @Test
  void testToEntity_WithEmptyCards_ShouldMapWithEmptyList() {
    userDto.setCards(new ArrayList<>());

    User result = userMapper.toEntity(userDto);

    assertNotNull(result, "Mapped entity should not be null");
    assertNotNull(result.getCards(), "Cards list should not be null");
    assertTrue(result.getCards().isEmpty(), "Cards list should be empty");
  }

  @Test
  void testToEntity_WithNullUserDto_ShouldReturnNull() {
    User result = userMapper.toEntity(null);
    assertNull(result, "Mapping null should return null");
  }
}