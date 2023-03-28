package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface WarningService {

    boolean isWarning(Integer id);

    SendMessage setWarning(Message message, User user);
}
