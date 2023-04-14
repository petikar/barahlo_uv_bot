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
    public SendMessage createSendMessageImportant(String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(configProperties.getImportantId());
        sendMessage.setText(text);

        return sendMessage;
    }

    @Override
    public SendMessage createSendMessageHistory(String text) {

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
                text.append
                        (String.format("%n%n" +
                                        "\uD83C\uDF4A текст: %s %n" +
                                        "\uD83C\uDF4A размер фото: %d %n" +
                                        "\uD83C\uDF4A дата отправки:  %s %n" +
                                        "\uD83C\uDF4A предупреждение:  %s",
                                dto.getText(),
                                dto.getPhotoSize(),
                                dto.getDate(),
                                dto.getIsWarning()
                                )
                        );
            }
            duplicates.add(createSendMessageImportant(String.format("От пользователя id = %d действуют следующие объявления: %s", id, text.toString())));
        }
        return duplicates;
    }

    @Override
    public Set<SendMessage> findDuplicatesAndSendMeListForCommercialSender(Long userId) {
        List<MessageDTO> messages = service.groupingMessagesByUserId(userId);

        Set<SendMessage> duplicates = new HashSet<>();
        StringBuilder text = new StringBuilder();

        for (MessageDTO dto : messages) {
            if (!dto.getText().equals("without text")) {
                text.append(
                        String.format("%n %n" +
                                        "‼️id сообщения: %s %n " +
                                        "‼️предупреждение: %s %n " +
                                        "‼️текст: %s %n " +
                                        "‼️дата отправки: %s",
                                dto.getIdMessage(),
                                dto.getIsWarning(),
                                dto.getText(),
                                dto.getDate()
                        )
                );

            }
        }
        duplicates.add(createSendMessageImportant(String.format("‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️" +
                        "От коммерческого пользователя id = %d " +
                        "действуют следующие объявления: %s \n " +
                        "‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️‼️",
                userId,
                text.toString())));
        return duplicates;
    }
}
