package petikar.barahlo_uv_bot.entity;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDTO toDto(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setBot(user.getIsBot());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        return dto;
    }

    @Override
    public User toEntity(UserDTO dto) {

        User user = new User();

        user.setId(dto.getId());
        user.setUserName(dto.getUserName());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsBot(dto.isBot());

        return user;
    }
}
