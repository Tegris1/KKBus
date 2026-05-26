package pasir.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pasir.dtos.UserDto;
import pasir.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);
    UserDto toDto(User user);
    User updateUser(UserDto dto, @MappingTarget User user);
}
