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
import pro.sky.telegrambot.messagesender.NewMessage;
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
    private final NewMessage newMessage;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            Long chatId = update.message().chat().id();
            try {
                log.info("Processing update: {}", update);
                String messageText = categoryValidator.validateAndClean(update.message().text().toLowerCase());
                log.info("Проверка сообщения прошла успешно: " + messageText);
                if (messageText.contains("/add")) {
                    String[] folderNames = messageText.split(" ");
                    if (folderNames.length == 3) {
                        addCategory.addChildFolder(folderNames[1], folderNames[2], chatId);
                    } else if (folderNames.length == 2 && !isNumeric(folderNames[1])) {
                        addCategory.addRootFolder(folderNames[1], chatId);
                    } else {
                        addCategory.addFolderById(Long.parseLong(folderNames[1]), chatId);
                    }
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
                    viewTreeCategory.viewTree(chatId);
                }
            } catch (IllegalArgumentException e) {
                newMessage.createNewMessage(chatId, "Команда должна начинаться с /<команда> и содержать параметры.");
                log.error(e.getMessage());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

}
