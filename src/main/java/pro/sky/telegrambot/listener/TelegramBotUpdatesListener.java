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
import pro.sky.telegrambot.validator.CategoryValidator;

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
    private final CategoryValidator categoryValidator;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                log.info("Processing update: {}", update);
                String messageText = categoryValidator.validateAndClean(update.message().text().toLowerCase());
                log.info("Проверка сообщения прошла успешно: " + messageText);
                Long chatId = update.message().chat().id();
                if (messageText.contains("/add")) {
                    addCategory.addElement(messageText, chatId);
                }
                if (messageText.startsWith("/del")) {
                    String params = messageText.split(" ")[1];
                    if (isNumeric(params)) {
                        removeCategory.removeFolderById(Long.parseLong(params), chatId);
                    } else {
                        removeCategory.findAllFoldersAndRemoveIfFolderIsUnique(params, chatId);
                    }
                }
                if (messageText.contains("/view")) {
                    viewTreeCategory.viewTree(messageText, chatId);
                }
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

}
