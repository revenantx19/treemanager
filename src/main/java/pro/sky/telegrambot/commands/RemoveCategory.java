package pro.sky.telegrambot.commands;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;
import pro.sky.telegrambot.validator.CategoryValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor

public class RemoveCategory {


    private final TelegramBot bot;
    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;
    private final CategoryValidator categoryValidator;

    private static boolean flag = false;

    public void removeElement(String messageText, Long chatId) {
        try {
            String folderName = categoryValidator.validateAndClean(messageText).split(" ")[0];
            if (isNumeric(folderName)) {
                newMessage.createNewMessage(chatId, "Сейчас изобретём логику удаления......");
            }
            List<String> allRemoveElements = treeManagerRepository.viewRemoveElements(folderName);
            log.info("Найдено папок " + allRemoveElements.size());
            if (allRemoveElements.size() > 1) {

                StringBuilder buildMessage = new StringBuilder();

                List<String> pathsRemovalCategories = treeManagerRepository.findPathByFolderName(folderName);

                newMessage.createNewMessage(chatId, "Мы нашли следующие каталоги.\n" +
                        "Выберите порядковый номер каталога, который надо удалить:\n" + buildMessage);

                //treeManagerRepository.delete(folderName);

            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            newMessage.createNewMessage(chatId, "Имя каталога содержит недопустимые символы.");
        }
        /**
         * сюда надо добавить просмотр всей базы на наличие двух одинаковых имён
         * и если они есть, то вывести список всех папок
         * и дать выбор пользователю на удаление
         *
         * Также надо обработать исключениями это место
         *
         */
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

}
