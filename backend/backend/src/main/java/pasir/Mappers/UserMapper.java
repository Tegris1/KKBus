package pasir.Mappers;

import org.mapstruct.Mapper;
import pasir.dtos.UserDto;
import pasir.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);
    UserDto toDto(User user);
    User updateUser(UserDto dto, User user);
    User updateUser(UserDto dto, String email);
}
