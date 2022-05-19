package petikar.barahlo_uv_bot.entity;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageMapper {

    MessageDTO toDto(Message message);
    Message toEntity(MessageDTO dto);

}
