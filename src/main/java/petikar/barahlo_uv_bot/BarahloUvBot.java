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

import static java.lang.Math.random;
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
                            update.getMessage().getText().startsWith("/warn") ||
                                    update.getMessage().getText().toLowerCase().contains("правила") ||
                                    update.getMessage().getText().toLowerCase().contains("где") ||
                                    update.getMessage().getText().toLowerCase().contains("посмотреть") ||
                                    update.getMessage().getText().toLowerCase().contains("размещение") ||
                                    update.getMessage().getText().toLowerCase().contains("повторное") ||
                                    update.getMessage().getText().toLowerCase().contains("?")
                    )
                    &&
                    update.getMessage().getFrom().getId().equals(Long.valueOf(myId))
            ) {
                Message message = update.getMessage().getReplyToMessage();
                try {
                    execute(warningService.setWarning(message, message.getFrom()));
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
                        execute(sendMessageService.createSendMessage("\uD83C\uDF4A\uD83C" +
                                "\uDF4A\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A\nВНИМАНИЕ, ДУБЛИ\n" +
                                "\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A\uD83C\uDF4A"));

                        for (MessageDTO message : messages) {
                            System.out.println(message);
                            String text = "ДУБЛЬ: \nНовое сообщение от пользователя c id =  " + update.getMessage().getFrom().getId() + " " + NameUtils.getFullName(update.getMessage().getFrom());
                            if (message.getText() != null) {
                                text = text + "\n" + message.getText() + " .";
                            }
                            execute(sendMessageService.createSendMessage(text));
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
                        for (SendMessage message : sendMessageService.findDuplicatesAndSendMeList(id)) {
                            try {
                                execute(message);
                                int sec = (int) (random() * 10000);
                                System.out.println("sleep: " + sec);
                                sleep(sec);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                                System.out.println("Ошибка в onUpdateReceived методе класса BarahloUvBot, метод c коммерческим сообщением");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
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
                Message message = update.getEditedMessage();
                String text = "UPD: \n" + MessageTextUtils.getTextFromMessage(message);
                message.setText(text);
                update.setMessage(message);
                execute(forwardMessageService.createForwardMessage(update));
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
