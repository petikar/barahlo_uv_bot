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

   // LocalDateTime deleteExceptLastWeek();

    void save(Message message);

    List<MessageDTO> findAll();

    List<MessageDTO> findAllExceptToday();

    Map<Long, List<MessageDTO>> groupingMessagesByUserId();

    List<MessageDTO> groupingMessagesByUserId(Long userId);

    Set<MessageDTO> findRepeatMessages();

    MessageDTO getMessageDTOById(Integer id);

    //TODO разобраться с обновлением в телеграмме (редактирование сообщения, его удаление)
}
