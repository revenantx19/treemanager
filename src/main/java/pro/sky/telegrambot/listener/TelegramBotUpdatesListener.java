package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.invoker.Invoker;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.validator.CategoryValidator;

import javax.annotation.PostConstruct;
import java.util.List;
/**
 * Слушатель обновлений Telegram-бота, обрабатывающий входящие обновления от пользователей.
 *
 * <p>Класс реализует интерфейс {@link UpdatesListener} и предназначен для обработки
 * обновлений, поступающих в Telegram-бота. Он принимает входящие сообщения,
 * валидирует и очищает их перед выполнением соответствующих команд.
 *
 * <p>При возникновении ошибок во время обработки обновлений, класс логирует
 * сообщение об ошибке для дальнейшего анализа.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final CategoryValidator categoryValidator;
    private final NewMessage newMessage;
    private final Invoker invoker;
    /**
     * Инициализирует слушатель обновлений Telegram-бота.
     *
     * <p>Устанавливает текущий экземпляр в качестве слушателя обновлений для
     * Telegram-бота, чтобы получать и обрабатывать обновления.
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    /**
     * Обрабатывает список обновлений, поступающих от Telegram.
     *
     * <p>Для каждого обновления создается экземпляр {@link MessageContext} с информацией
     * о сообщении и его параметрами. Затем выполняется соответствующая команда с помощью
     * {@link Invoker}. Если возникает исключение во время обработки, класс логирует ошибку.
     *
     * @param updates список обновлений {@link Update}, полученных от Telegram
     * @return статус подтверждения обработки обновлений
     */
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                MessageContext messageContext = new MessageContext(update, categoryValidator.validateAndClean(update.message().text()));
                invoker.runCommand(messageContext);
            } catch (Exception e) {
                log.error(newMessage.createNewMessage(update.message().chat().id(), e.getMessage()));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
