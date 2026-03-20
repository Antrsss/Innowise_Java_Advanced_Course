package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CardMapper {
  @Mapping(source = "user.id", target = "userId")
  PaymentCardDto toDto(PaymentCard card);

  @Mapping(target = "user.id", source = "userId")
  PaymentCard toEntity(PaymentCardDto cardDto);

  default User mapUser(Long userId) {
    if (userId == null) {
      return null;
    }
    User user = new User();
    user.setId(userId);
    return user;
  }
}
