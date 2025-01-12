package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.invoker.Invoker;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.validator.CategoryValidator;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final CategoryValidator categoryValidator;
    private final NewMessage newMessage;
    private final Invoker invoker;
    private final HttpMessageConverters messageConverters;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

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
