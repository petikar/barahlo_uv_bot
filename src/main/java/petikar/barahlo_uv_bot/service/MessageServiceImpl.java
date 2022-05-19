package petikar.barahlo_uv_bot.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.entity.MessageMapper;
import petikar.barahlo_uv_bot.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    MessageRepository repository;
    private final MessageMapper mapper = Mappers.getMapper(MessageMapper.class);

    public MessageServiceImpl(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void deleteExceptLastWeek() {
        repository.deleteMessageDTOByDateBefore(LocalDateTime.now().minusDays(7));
    }

    @Override
    public void save(Message message) {
        MessageDTO messageDTO = mapper.toDto(message);
        if (messageDTO.getText().isEmpty()) {
            messageDTO.setText("without text");
        }
        repository.save(messageDTO);
    }

    @Override
    public Set<MessageDTO> findRepeatMessages() {
        deleteExceptLastWeek();
        Set<MessageDTO> repeatMessages = findRepeatMessagesByUserId();
        Set<MessageDTO> repeatMessagesByText = findRepeatMessagesByText(repeatMessages);
        Set<MessageDTO> repeatMessagesByPhotoSize = findRepeatMessagesByPhotoSize(repeatMessages);
        repeatMessagesByText.addAll(repeatMessagesByPhotoSize);
        return repeatMessagesByText;

    }

    //TODO продумать алгоритм, слишком сложно тут придумала.
    //TODO запрос списка по каждому пользователю при наличии повторяющихся сообщений (не сами сообщения пересылать)
    private Set<MessageDTO> findRepeatMessagesByUserId() {
        List<MessageDTO> list = (List<MessageDTO>) repository.findAll();
        Set<MessageDTO> duplicates = new HashSet<>();

        Map<Long, List<MessageDTO>> mapGroupingByIdUser = list.stream()
                .collect(Collectors.groupingBy(MessageDTO::getIdUser));
        for (Long id : mapGroupingByIdUser.keySet()) {
            List<MessageDTO> messageDTOList = mapGroupingByIdUser.get(id);
            if (messageDTOList.size() > 1) {
                duplicates.addAll(messageDTOList);
            }
        }

        return duplicates;

    }

    private Set<MessageDTO> findRepeatMessagesByText(Set<MessageDTO> repeatMessages) {

        Set<MessageDTO> duplicates = new HashSet<>();

        //TODO не точное соответствие текста проверять

        Map<String, List<MessageDTO>> mapGroupingByText = repeatMessages.stream()
                .collect(Collectors.groupingBy(MessageDTO::getText));
        for (String text : mapGroupingByText.keySet()) {
            if (!text.equals("without text")) {
                List<MessageDTO> messageDTOList = mapGroupingByText.get(text);
                if (messageDTOList.size() > 1) {
                    duplicates.addAll(messageDTOList);
                }
            }
        }

        return duplicates;
    }

    private Set<MessageDTO> findRepeatMessagesByPhotoSize(Set<MessageDTO> repeatMessages) {

        Set<MessageDTO> duplicates = new HashSet<>();

        Map<Integer, List<MessageDTO>> mapGroupingByPhotoSize = new HashMap<>();

        for (MessageDTO messageDTO : repeatMessages) {
            Integer size = messageDTO.getPhotoSize();
            if (size != null) {
                List<MessageDTO> list;
                if (mapGroupingByPhotoSize.containsKey(size)) {
                    list = mapGroupingByPhotoSize.get(size);
                } else {
                    list = new ArrayList<>();
                }
                list.add(messageDTO);
                mapGroupingByPhotoSize.put(size, list);
            }
        }

        // делаю список, где все дубликаты по размеру
        for (Integer size : mapGroupingByPhotoSize.keySet()) {
            List<MessageDTO> messageDTOList = mapGroupingByPhotoSize.get(size);
            if (messageDTOList.size() > 1) {
                duplicates.addAll(messageDTOList);
            }
        }

        return duplicates;
    }

}
