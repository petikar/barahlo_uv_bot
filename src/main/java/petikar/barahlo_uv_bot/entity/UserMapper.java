package petikar.barahlo_uv_bot.entity;

import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.User;

@Mapper
public interface UserMapper {

    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}
