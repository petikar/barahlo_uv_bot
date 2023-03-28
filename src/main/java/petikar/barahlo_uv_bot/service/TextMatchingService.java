package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.util.List;

public interface TextMatchingService {
    List<MessageDTO> findAndSendReallySimilarMessage(Update update);
}
