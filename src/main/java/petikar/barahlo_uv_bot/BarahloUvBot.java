package petikar.barahlo_uv_bot;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.service.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;

@Component

public class BarahloUvBot extends TelegramLongPollingBot {

    //TODO сделать иерархию классов, слишком много тут всего

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.myId}")
    private String myId;

    SendMessageService sendMessageService;
    ForwardMessageService forwardMessageService;
    TextMatchingService textMatchingService;
    CommercialService commercialService;
    MessageService messageService;
    WarningService warningService;

    public BarahloUvBot(SendMessageService sendMessageService,
                        ForwardMessageService forwardMessageService,
                        TextMatchingService textMatchingService,
                        CommercialService commercialService,
                        MessageService messageService,
                        WarningService warningService) {
        this.sendMessageService = sendMessageService;
        this.forwardMessageService = forwardMessageService;
        this.textMatchingService = textMatchingService;
        this.commercialService = commercialService;
        this.messageService = messageService;
        this.warningService = warningService;
    }


//    LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

    @Override
    public void onUpdateReceived(Update update) {

        //TODO заменить на логирование

        System.out.println("\n\n\nПолучено ОБНОВЛЕНИЕ " + LocalDateTime.now() + ":\n");
        System.out.println(update);
        System.out.println("\n\n\n");

        if (update.hasMessage()) {
            if (update.getMessage().hasText() && update.getMessage().getText().equals("/findAndForward")) {
                for (ForwardMessage message : forwardMessageService.findAndForwardDuplicates()) {
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        //TODO часто ошибка Error forwarding message: [400] Bad Request: message to forward not found
                        System.out.println("Ошибка в onUpdateReceived методе класса BarahloUvBot, метод /findAndForward");
                    }
                }

            }
            // установка статуса коммерческого пользователя
            else if (update.getMessage().hasText() &&
                    update.getMessage().getText().equalsIgnoreCase("коммерческое") &&
                    update.getMessage().getFrom().getId().equals(Long.valueOf(myId))
            ) {
                Message message = update.getMessage().getReplyToMessage();
                //commercialSet.add(message)
                try {
                    //TODO добавлять коммерческих в очередь без повторов, и когда проходит время какое-то, делать проверку:
                    execute(commercialService.setCommercial(message, message.getFrom()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            // установка статуса предупреждения
            else if (update.getMessage().hasText()
                    &&
                    (
                            //TODO: вынести проверку эту в отдельный метод
                            //TODO: добавить разные предупреждения по разным причинам (в дто)
                            update.getMessage().getText().startsWith("/warn") ||
                                    update.getMessage().getText().toLowerCase().contains("правила") ||
                                    update.getMessage().getText().toLowerCase().contains("где") ||
                                    update.getMessage().getText().toLowerCase().contains("посмотреть") ||
                                    update.getMessage().getText().toLowerCase().contains("фото") ||
                                    update.getMessage().getText().toLowerCase().contains("повторное") ||
                                    update.getMessage().getText().toLowerCase().contains("?")
                    )
                    &&
                    update.getMessage().getFrom().getId().equals(Long.valueOf(myId))
            ) {
                Message message = update.getMessage().getReplyToMessage();
                String textFromUpdate = update.getMessage().getText().toLowerCase();
                StringBuilder reason = new StringBuilder();
                if (textFromUpdate.contains("2.1") ||
                        textFromUpdate.contains("3.3") ||
                        textFromUpdate.contains("что")
                ) {
                    reason.append("2.1, 3.3 Название ");
                }
                if (textFromUpdate.contains("1.4") ||
                        textFromUpdate.contains("коррект")
                ) {
                    reason.append("1.4 Цена не корректна ");
                }
                if (textFromUpdate.contains("переписки")) {
                    reason.append("1.5 Переписки ");
                }
                if (textFromUpdate.contains("лекарств")) {
                    reason.append("1.2 Лекарства ");
                }
                if (textFromUpdate.contains("2.2") ||
                        textFromUpdate.contains("цена") ||
                        textFromUpdate.contains("цены") ||
                        textFromUpdate.contains("цену")
                ) {
                    reason.append("2.2, 3.2 Цена не указана ");
                }
                if (textFromUpdate.contains("фото")) {
                    reason.append("2.3, 3.1 Много фото ");
                }
                if (textFromUpdate.contains("повтор")) {
                    reason.append("2.5, 3.4 Повторное размещение ");
                }
                if (textFromUpdate.contains("где") ||
                        textFromUpdate.contains("доставка")
                ) {
                    reason.append("3.5, 2.4 Доставка, район ");
                }
                if (textFromUpdate.contains("част")                ) {
                    reason.append("2.6, 3.7 Несколькими частями ");
                }
                try {
                    execute(warningService.setWarning(message, message.getFrom(), String.valueOf(reason)));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                // обработка команды /groupByUserId
            } else if (update.getMessage().hasText() && update.getMessage().getText().equals("/groupByUserId")) {

                for (SendMessage message : sendMessageService.findDuplicatesAndSendMeList()) {
                    try {

                        int sec = (int) (random() * 10000);
                        System.out.println("sleep: " + sec);
                        sleep(sec);
                        execute(message);

                    } catch (TelegramApiException | InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Ошибка в onUpdateReceived методе класса BarahloUvBot, метод /groupByUserId");
                    }
                }

                System.out.println("----------------------------------- Класс BarahloUvBot, метод /groupByUserId -----------------------------------\n----------------------------------- закончил работу в " + LocalDateTime.now() + "-----------------------------------");
                // поиск дублей
            } else if ((update.getMessage().hasText() || update.getMessage().hasPhoto()) /*&&
                    !update.getMessage().getFrom().getId().equals((Long.valueOf(myId)))*/
            ) {
                // если надо тестировать на мне - убрать верхнюю строку.

                //TODO пересмотреть алгоритм поиска дублей

                try {
                    List<MessageDTO> messages = textMatchingService.findAndSendReallySimilarMessage(update);

                    if (messages.size() > 0) {
                        execute(sendMessageService.createSendMessageImportant("\uD83C\uDF4A\uD83C" +
                                "\uDF4A\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A\nВНИМАНИЕ, ДУБЛИ\n" +
                                "\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A"));

                        for (MessageDTO message : messages) {
                            System.out.println(message);
                            String text = "ДУБЛЬ: \nНовое сообщение от пользователя c id =  " + update.getMessage().getFrom().getId() + " " + NameUtils.getFullName(update.getMessage().getFrom());
                            if (message.getText() != null) {
                                text = text + "\n" + message.getText() + " .";
                            }
                            execute(sendMessageService.createSendMessageImportant(text));
                            int sec = (int) (random() * 10000);
                            System.out.println("sleep: " + sec);
                            sleep(sec);
                        }
                    }
                } catch (TelegramApiException | InterruptedException e) {
                    e.printStackTrace();
                }

                //сохранение в базу и после проверка всех действующих коммерческих объявлений
                try {
                    execute(forwardMessageService.createForwardMessage(update));

                    long id = update.getMessage().getFrom().getId();

                    //вытащить это в новый поток TODO переработать метод, чтоб дожидался нескольких сообщений
                    if (commercialService.isCommercial(id)) {
                        // если есть текст, не только фото
                        // то выдавать список всех действующих объявлений
                        String text = MessageTextUtils.getTextFromMessage(update.getMessage());
                        if (!text.equals("")) {
                            for (SendMessage message : sendMessageService.findDuplicatesAndSendMeListForCommercialSender(id)) {
                                try {
                                    if (message.getText().length() > 4096) {
                                        int len = message.getText().length();

                                        System.out.println("len " + len);
                                        int beginIndex = 0;
                                        int endIndex = 4096;
                                        // for (int i = 0; i<len; i++) {
                                        String startText = message.getText();
                                        while (endIndex <= len && beginIndex < endIndex) {

                                            //TODO логирование

                                            System.out.println("\nделю\n");
                                            System.out.println("beginIndex " + beginIndex);
                                            System.out.println("endIndex " + endIndex);

                                            String newText = startText.substring(beginIndex, endIndex);
                                            message.setText(newText);
                                            execute(message);

                                            beginIndex = endIndex;
                                            endIndex = min(beginIndex + 4096, len);

                                            int sec = (int) (random() * 10000);
                                            System.out.println("sleep: " + sec);
                                            sleep(sec);
                                        }
                                    } else {
                                        execute(message);
                                        int sec = (int) (random() * 10000);
                                        System.out.println("sleep: " + sec);
                                        sleep(sec);
                                    }
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                    System.out.println("Ошибка в onUpdateReceived методе класса BarahloUvBot, метод c коммерческим сообщением");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    ////////////////////////////////////////////////////////////////
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasEditedMessage()) {
            try {
                ForwardMessage forwardMessage = forwardMessageService.createForwardMessageFromEdited(update);
                if (forwardMessage != null) {
                    System.out.println("forwardMessage не ноль");
                    execute(sendMessageService.createSendMessageImportant("\uD83D\uDC30 Сообщение изменено:"));
                    execute(forwardMessage);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
