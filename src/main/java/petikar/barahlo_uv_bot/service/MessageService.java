package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MessageService {

    /**
     * This service is used for operations with database(repository)
     */

    void deleteExceptLastWeek();

    void save(Message message);

    Set<MessageDTO> findAll();

    Map<Long, List<MessageDTO>> groupingMessagesByUserId();

    Set<MessageDTO> findRepeatMessages();

    //TODO разобраться с обновлением в телеграмме (редактирование сообщения, его удаление)
}
