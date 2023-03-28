package petikar.barahlo_uv_bot.entity;

import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.Message;

@Mapper
public interface MessageMapper {

    MessageDTO toDto(Message message);
    Message toEntity(MessageDTO dto);

}
