package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import petikar.barahlo_uv_bot.entity.*;
import petikar.barahlo_uv_bot.repository.MessageRepository;
import petikar.barahlo_uv_bot.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class CommercialServiceImpl implements CommercialService {

    UserRepository userRepository;
    MessageRepository messageRepository;

    MessageMapper messageMapper;
    UserMapper userMapper;

    ForwardMessageService forwardMessageService;
    SendMessageService sendMessageService;


    public CommercialServiceImpl(UserRepository userRepository,
                                 MessageRepository messageRepository,
                                 MessageMapper messageMapper,
                                 UserMapper userMapper,
                                 ForwardMessageService forwardMessageService,
                                 SendMessageService sendMessageService) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.forwardMessageService = forwardMessageService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean isCommercial(Long id) {
        return userRepository.getIsCommercialById(id);
    }

    @Override
    @Transactional
    public SendMessage setCommercial(Message message, User user) {

        //Сюда передавать сообщение, на которое ответ         message.getReplyToMessage();

        UserDTO userDTO = userMapper.toDto(user);
        MessageDTO messageDTO = messageMapper.toDto(message);

        Boolean warning = messageRepository.getById(messageDTO.getIdMessage()).getIsWarning();

        Set<MessageDTO> commercialSet = new HashSet<>();

        commercialSet.add(messageDTO);

        userDTO.setCommercial(true);

        if (messageDTO.getMediaGroupId()!=null) {
            commercialSet.addAll(messageRepository.findAllByMediaGroupId(messageDTO.getMediaGroupId()));
        }
        userRepository.save(userDTO);

        for (MessageDTO mDTO: commercialSet) {
            mDTO.setLabel(Label.COMMERCIAL.name());
            mDTO.setIsWarning(warning);
            messageRepository.save(mDTO);
        }

        SendMessage sendMessage = sendMessageSetCommercial(messageDTO, userDTO);

        return sendMessage;
    }

    private SendMessage sendMessageSetCommercial(MessageDTO messageDTO, UserDTO userDTO) {

        //TODO не ставится тег коммерческого при пересланном сообщении. Посмотри, как отображается пересланное пересланное сообщение в телеграмме

        String userName = userDTO.getUserName() + " " + userDTO.getFirstName() + " " + userDTO.getLastName();

        messageDTO.setText("Пользователю " + userName + " с id = " + userDTO.getId() + " установлен статус коммерческого пользователя");

        SendMessage sendMessage = sendMessageService.createSendMessage(" \uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\nПользователю " + userName + " с id = " + userDTO.getId() + " установлен статус коммерческого пользователя\n\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40\uD83D\uDC40");

        return sendMessage;
    }
}
