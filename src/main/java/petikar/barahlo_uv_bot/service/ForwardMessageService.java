package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ForwardMessageService {
    ForwardMessage forwardMessage(Update update);
}
