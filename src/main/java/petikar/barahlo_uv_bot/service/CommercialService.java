package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface CommercialService {
    // TODO добавить список id, занимающихся коммерческой деятельносьтю:
    // метод внесения id в коммерческую таблицу
    // проверка сколько сообщений действует только от тех, кто есть в коммерческой таблице

    boolean isCommercial(Long id);

    SendMessage setCommercial(Message message, User user);


}
