package by.zgirskaya.advanced_course.mapper;

import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CardMapper {
  PaymentCardDto toDto(PaymentCard card);

  @Mapping(target = "user.id", source = "userId")
  PaymentCard toEntity(PaymentCardDto cardDto);
}
