package petikar.barahlo_uv_bot.service;

import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.util.Set;

public interface MessageService {

    void deleteExceptLastWeek();

    @Transactional
    Set<MessageDTO> findRepeatMessages();

    void save(Message message);
}
