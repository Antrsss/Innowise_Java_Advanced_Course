package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

  private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @Test
  @DisplayName("Should map User to UserDto with cards collection")
  void testToDtoWithCards() {
    User user = new User();
    user.setId(1L);

    PaymentCard card = new PaymentCard();
    card.setId(10L);
    card.setNumber("1111222233334444");
    user.setCards(List.of(card));

    UserDto dto = mapper.toDto(user);

    assertThat(dto).isNotNull();
    assertThat(dto.getCards()).hasSize(1);
    assertThat(dto.getCards().getFirst().getId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("Should map UserDto to User with cards collection")
  void testToEntityWithCards() {
    UserDto dto = new UserDto();
    dto.setId(2L);

    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setId(20L);
    dto.setCards(List.of(cardDto));

    User user = mapper.toEntity(dto);

    assertThat(user).isNotNull();
    assertThat(user.getCards()).hasSize(1);
    assertThat(user.getCards().get(0).getId()).isEqualTo(20L);
  }

  @Test
  @DisplayName("Should handle null and empty collections in User")
  void testToDtoCollections() {
    User user = new User();

    user.setCards(null);
    assertThat(mapper.toDto(user).getCards()).isNull();

    user.setCards(Collections.emptyList());
    assertThat(mapper.toDto(user).getCards()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null and empty collections in UserDto")
  void testToEntityCollections() {
    UserDto dto = new UserDto();

    dto.setCards(null);
    assertThat(mapper.toEntity(dto).getCards()).isNull();

    dto.setCards(Collections.emptyList());
    assertThat(mapper.toEntity(dto).getCards()).isEmpty();
  }

  @Test
  @DisplayName("Should return null when mapping null objects")
  void testNullMappings() {
    assertThat(mapper.toDto(null)).isNull();
    assertThat(mapper.toEntity(null)).isNull();
  }
}