package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import petikar.barahlo_uv_bot.ConfigProperties;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SendMessageServiceImpl implements SendMessageService {

    private final MessageService service;

    private final ConfigProperties configProperties;

    public SendMessageServiceImpl(MessageService service, ConfigProperties configProperties) {
        this.service = service;
        this.configProperties = configProperties;
    }

    @Override
    public SendMessage createSendMessage(String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(configProperties.getHistoryId());
        sendMessage.setText(text);

        return sendMessage;
    }

    @Override
    public Set<SendMessage> findDuplicatesAndSendMeList() {
        Map<Long, List<MessageDTO>> messages = service.groupingMessagesByUserId();
        Set<SendMessage> duplicates = new HashSet<>();
        for (Long id : messages.keySet()) {
            //TODO add name and ets
            StringBuilder text = new StringBuilder();
            for (MessageDTO dto : messages.get(id)) {
                text.append(String.format("%n%nтекст: %s %nразмер фото: %d", dto.getText(), dto.getPhotoSize()));
            }

            duplicates.add(createSendMessage(String.format("От пользователя id = %d действуют следующие объявления: %s", id, text.toString())));
        }
        return duplicates;
    }

    ;

}
