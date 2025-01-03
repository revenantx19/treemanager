package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.commands.AddCategory;
import pro.sky.telegrambot.commands.RemoveCategory;
import pro.sky.telegrambot.commands.ViewTreeCategory;
import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final AddCategory addCategory;
    private final RemoveCategory removeCategory;
    private final ViewTreeCategory viewTreeCategory;
    private final TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                log.info("Processing update: {}", update);
                // ловим отправленное сообщение
                String messageText = update.message().text();
                Long chatId = update.message().chat().id();
                if (messageText.contains("/add")) {
                    addCategory.addElement(messageText, chatId);
                }
                if (messageText.contains("/del")) {
                    removeCategory.removeElement(messageText, chatId);
                }
                if (messageText.contains("/view")) {
                    viewTreeCategory.viewTree(messageText, chatId);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
