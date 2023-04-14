package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.entity.UserDTO;
import petikar.barahlo_uv_bot.entity.UserMapper;

import java.util.*;

import static me.xdrop.fuzzywuzzy.FuzzySearch.*;

@Service
public class TextMatchingServiceImpl implements TextMatchingService {

    private final MessageService service;
    private final UserMapper userMapper;

    // сообщения с какой длиной начинаем учитывать при поиске дублей
    private final int testLength = 9;

    public TextMatchingServiceImpl(MessageService service, UserMapper userMapper) {
        this.service = service;
        this.userMapper = userMapper;
    }

    @Override
    public List<MessageDTO> findAndSendReallySimilarMessage(Update update) {

        List<MessageDTO> result = new ArrayList<>();

        List<MessageDTO> messageDTOs = service.findAllExceptToday();

        Message message = update.getMessage();

        String text = MessageTextUtils.getTextFromMessage(message);

        ;

        UserDTO userDTO = userMapper.toDto(update.getMessage().getFrom());

//действия ниже распараллелить
        Optional<List<PhotoSize>> optional = Optional.ofNullable(update.getMessage().getPhoto());
        if (optional.isPresent()) {
            int size = message.getPhoto().size() - 1;
            Integer photoSize = update.getMessage().getPhoto().get(size).getFileSize();
            result.addAll(findAndSendReallySimilarMessageByPhotoSize(messageDTOs, photoSize));
        }

        if (text != null && text.length() > testLength) {
            result.addAll(findAndSendReallySimilarMessageByText(messageDTOs, userDTO, text));
        }

        return result;
    }

    private Set<MessageDTO> findAndSendReallySimilarMessageByPhotoSize(List<MessageDTO> messageDTOs, int photoSize) {

        Set<MessageDTO> resultSet = new HashSet<>();

        for (MessageDTO messageDTO : messageDTOs) {
            if (messageDTO.getPhotoSize() != null) {
                if (messageDTO.getPhotoSize() == photoSize) {
                    messageDTO.setText(
                            "\uD83C\uDF06\uD83C\uDF06\uD83C\uDF06\uD83C\uDF06\uD83C\uDF06\n Соответствие по размеру фото: \n" +
                                    "\uD83C\uDF06 IdMessage = " + messageDTO.getIdMessage() + "\n" +
                                    "\uD83C\uDF06 MediaGroupId = " + messageDTO.getMediaGroupId() + "\n" +
                                    "\uD83C\uDF06 Размер фото = " + photoSize + "\n" +
                                    "\uD83C\uDF06 Ранее размещал пользователь с id = " + messageDTO.getIdUser() + "\n" +
                                    "\uD83C\uDF06 Прежняя дата размещения = " + "\n" + messageDTO.getDate()
                    );
                    //TODO из другой таблицы достать имя пользователя

                    resultSet.add(messageDTO);
                }
            }
        }
        return resultSet;
    }

    private Set<MessageDTO> findAndSendReallySimilarMessageByText(List<MessageDTO> messageDTOs, UserDTO userDTO, String text) {

        Set<MessageDTO> resultSet = new HashSet<>();

        for (MessageDTO messageDTO : messageDTOs) {
            if (messageDTO != null && messageDTO.getText() != null) {
                if (!messageDTO.getText().equals("without text")) {

                    //TODO Выбрать лучший показатель похожести
                    if (messageDTO.getText().length() > testLength) {
                        // int resultRatio = ratio(text, messageDTO.getText());
                        // int resultTokenSortPartialRatio = tokenSortPartialRatio(text, messageDTO.getText());
                        int resultTokenSetRatio = tokenSetRatio(text, messageDTO.getText());
                        //int resultWeightedRatio = weightedRatio(text, messageDTO.getText());

                        String newText;

                        if ((resultTokenSetRatio > 65) && (userDTO.getId().equals(messageDTO.getIdUser()))) {

                            int resultRatio = ratio(text, messageDTO.getText());
                            int resultTokenSortPartialRatio = tokenSortPartialRatio(text, messageDTO.getText());
                            int resultWeightedRatio = weightedRatio(text, messageDTO.getText());

                            String fullName = NameUtils.getFullNameFromDTO(userDTO);

                            newText = "\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\n" +
                                    "Ранее сообщение было размещено тем же пользователем, с id = "
                                    + messageDTO.getIdUser() + " " + fullName + "\n";

                            messageDTO.setText(newText +
                                    "\uD83E\uDDDA\u200D♂ result tokenSetRatio = " + resultTokenSetRatio + "%\n" +
                                    "resultRatio = " + resultRatio + "%\n" +
                                    "resultTokenSortPartialRatio = " + resultTokenSortPartialRatio + "%\n" +
                                    "resultWeightedRatio = " + resultWeightedRatio + "%\n" +
                                    "\uD83E\uDDDA\u200D♂ IdMessage = " + messageDTO.getIdMessage() + "\n" +
                                    "\uD83E\uDDDA\u200D♂ \uD83E\uDDDA\u200D♂ \uD83E\uDDDA\u200D♂ Новый размещённый текст = " + text + "\n" +
                                    "\uD83E\uDDDA\u200D♂ \uD83E\uDDDA\u200D♂ \uD83E\uDDDA\u200D♂ Текст, размещённый ранее = " + messageDTO.getText() + "\n" +
                                    "\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\n " +
                                    "\uD83E\uDDDA\u200D♂ Метка предупреждения, поставленная ранее = " + messageDTO.getIsWarning() + "\n" +
                                    "\uD83E\uDDDA\u200D♂ Прежняя дата размещения = " + messageDTO.getDate()
                            );
                            resultSet.add(messageDTO);
                        } else if (resultTokenSetRatio > 88) {

                            int resultRatio = ratio(text, messageDTO.getText());
                            int resultTokenSortPartialRatio = tokenSortPartialRatio(text, messageDTO.getText());
                            int resultWeightedRatio = weightedRatio(text, messageDTO.getText());

                            newText = "Совпадение с сообщением ДРУГОГО ПОЛЬЗОВАТЕЛЯ\n" +
                                    "\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\n Ранее размещал пользователь с id = " + messageDTO.getIdUser() + "\n";
                            messageDTO.setText(newText +
                                    "\uD83D\uDC39 result = " + resultTokenSetRatio + "%\n" +
                                    "resultRatio = " + resultRatio + "%\n" +
                                    "resultTokenSortPartialRatio = " + resultTokenSortPartialRatio + "%\n" +
                                    "resultWeightedRatio = " + resultWeightedRatio + "%\n" +
                                    "\uD83D\uDC39 IdMessage = " + messageDTO.getIdMessage() + "\n" +
                                    "\uD83D\uDC39 Новый размещённый текст = " + text + "\n" +
                                    "\uD83D\uDC39 Текст, размещённый ранее = " + messageDTO.getText() + "\n" +
                                    "\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\n Метка предупреждения, поставленная ранее = " + messageDTO.getIsWarning() + "\n" +
                                    "\uD83D\uDC39 Прежняя дата размещения = " + messageDTO.getDate()
                            );
                            resultSet.add(messageDTO);
                        }
                    }
                }
            }
        }
        return resultSet;
    }
}
