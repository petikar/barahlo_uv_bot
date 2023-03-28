package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import petikar.barahlo_uv_bot.entity.*;
import petikar.barahlo_uv_bot.repository.MessageRepository;
import petikar.barahlo_uv_bot.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    //TODO много сервисов, продумать, какие и сколько надо

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, MessageMapper messageMapper, UserMapper userMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void save(Message message) {
        MessageDTO messageDTO = messageMapper.toDto(message);
        UserDTO userDTO = userMapper.toDto(message.getFrom());
        if (messageDTO.getText().isEmpty()) {
            messageDTO.setText("without text");
        }

        if (message.getEditDate() != null){
            messageDTO.setEditDate(DateConverter.intToDate(message.getEditDate()));
            MessageDTO m = messageRepository.getById(messageDTO.getIdMessage());

            String label = m.getLabel();
            if (label!=null) {
                messageDTO.setLabel(label);
            }

            Boolean warning = m.getIsWarning();
            if (warning!=null) {
                messageDTO.setIsWarning(warning);
            }

            messageDTO.setIsWarning(m.getIsWarning());
        }

        messageRepository.save(messageDTO);

        if (!userRepository.existsById(userDTO.getId())) {
            userRepository.save(userDTO);
        }

    }

    @Override
    public List<MessageDTO> findAll() {

        return messageRepository.findAllByDateAfter(LocalDateTime.now().minusDays(6));
    }

    /**
     * This method represents the whole cycle of searching data and formation data about repeated messages
     *
     * @return Set<MessageDTO>
     */
    @Override
    //TODO   @Transactional
    public Set<MessageDTO> findRepeatMessages() {

        //    deleteExceptLastWeek();

        Set<MessageDTO> repeatMessagesByUserId = findRepeatMessagesByUserId();

        Set<MessageDTO> repeatMessages = findRepeatMessagesByText(repeatMessagesByUserId);
        Set<MessageDTO> repeatMessagesByPhotoSize = findRepeatMessagesByPhotoSize(repeatMessagesByUserId);
        repeatMessages.addAll(repeatMessagesByPhotoSize);
        return repeatMessages;

    }

    @Override
    public Map<Long, List<MessageDTO>> groupingMessagesByUserId() {

        List<MessageDTO> allDTOs = findAll();

        Map<Long, List<MessageDTO>> mapGroupingByIdUser = allDTOs.stream()
                .collect(Collectors.groupingBy(MessageDTO::getIdUser));

        Map<Long, List<MessageDTO>> result = new HashMap<>();

        for (Long id : mapGroupingByIdUser.keySet()) {
            List<MessageDTO> messageDTOList = mapGroupingByIdUser.get(id);
            //меня интересуют только сообщения тех пользователей, которые публиковали объявление больше одного раза
            if (messageDTOList.size() > 1) {
                result.put(id, messageDTOList);
            }
        }
        return result;
    }

    @Override
    public List<MessageDTO> groupingMessagesByUserId(Long userId) {

        List<MessageDTO> result = messageRepository.findByIdUser(userId);

        return result;
    }


    //TODO продумать алгоритм, слишком сложно тут придумала.
    //TODO запрос списка по каждому пользователю при наличии повторяющихся сообщений (не сами сообщения пересылать)
    private Set<MessageDTO> findRepeatMessagesByUserId() {

        Set<MessageDTO> duplicates = new HashSet<>();

        Map<Long, List<MessageDTO>> mapGroupingByIdUser = groupingMessagesByUserId();
        for (Long id : mapGroupingByIdUser.keySet()) {
            duplicates.addAll(mapGroupingByIdUser.get(id));
        }

        return duplicates;
    }

    //TODO продумать алгоритм, слишком сложно тут придумала.
    //TODO запрос списка по каждому пользователю при наличии повторяющихся сообщений (не сами сообщения пересылать)
    private List<MessageDTO> findRepeatMessagesByUserId(Long userId) {

        List<MessageDTO> mapGroupingByIdUser = groupingMessagesByUserId(userId);

        return mapGroupingByIdUser;
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
