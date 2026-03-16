package by.zgirskaya.advanced_course.mapper;

import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
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
