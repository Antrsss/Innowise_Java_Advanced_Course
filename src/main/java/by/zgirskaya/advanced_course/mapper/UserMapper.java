package by.zgirskaya.advanced_course.mapper;

import by.zgirskaya.advanced_course.dto.UserDto;
import by.zgirskaya.advanced_course.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);
  User toEntity(UserDto userDto);
}
