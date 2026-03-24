package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class CardMapperTest {

  private final CardMapper mapper = Mappers.getMapper(CardMapper.class);

  @Test
  @DisplayName("Should map PaymentCard to PaymentCardDto (check userId)")
  void testToDto() {
    User user = new User();
    user.setId(10L);

    PaymentCard card = new PaymentCard();
    card.setId(1L);
    card.setNumber("1234123412341234");
    card.setUser(user);

    PaymentCardDto dto = mapper.toDto(card);

    assertThat(dto).isNotNull();
    assertThat(dto.getUserId()).isEqualTo(10L);
    assertThat(dto.getNumber()).isEqualTo(card.getNumber());
  }

  @Test
  @DisplayName("Should map PaymentCardDto to PaymentCard (check user entity creation)")
  void testToEntity() {
    PaymentCardDto dto = new PaymentCardDto();
    dto.setId(1L);
    dto.setUserId(50L);
    dto.setNumber("4321432143214321");

    PaymentCard card = mapper.toEntity(dto);

    assertThat(card).isNotNull();
    assertThat(card.getUser()).isNotNull();
    assertThat(card.getUser().getId()).isEqualTo(50L);
    assertThat(card.getNumber()).isEqualTo(dto.getNumber());
  }

  @Test
  @DisplayName("Should test manual mapUser logic including null branch")
  void testMapUser() {
    assertThat(mapper.mapUser(null)).isNull();

    User user = mapper.mapUser(100L);
    assertThat(user).isNotNull();
    assertThat(user.getId()).isEqualTo(100L);
  }

  @Test
  @DisplayName("Should return null when mapping null card objects")
  void testNullMappings() {
    assertThat(mapper.toDto(null)).isNull();
    assertThat(mapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("Should handle PaymentCard with null User in toDto")
  void testToDtoWithNullUser() {
    PaymentCard card = new PaymentCard();
    card.setUser(null);

    PaymentCardDto dto = mapper.toDto(card);

    assertThat(dto).isNotNull();
    assertThat(dto.getUserId()).isNull();
  }
}