package pro.sky.telegrambot.commands;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TreeManagerCommands {

    private final TelegramBot bot;
    private final TreeManagerRepository treeManagerRepository;

    public void removeElement(String messageText, Update update) {
        String[] category = messageText.split(" ");
        String folderName = category[1];
        List<String> allRemoveElements = treeManagerRepository.viewRemoveElements(folderName);
        log.info("Найдено папок " + allRemoveElements.size());
        if (allRemoveElements.size() > 1) {
            createNewMessage(update, "Мы нашли следующие папки." +
                    "\nВыберите, какие из них надо удалить: ");
            StringBuilder buildMessage = new StringBuilder();
            for (String removeElement : allRemoveElements) {
                System.out.println(buildMessage.append(removeElement + "\n"));
            }
            int i = 0;
            for (String foundFolder : treeManagerRepository.findDirectoriesByName(folderName)) {
                buildMessage.append(i++ + ". " + foundFolder + "\n");
            }
            bot.execute(new SendMessage(update.message().chat().id(),
                    buildMessage.toString()));
            //treeManagerRepository.delete(folderName);

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

    public void viewTree() {
        //treeManagerRepository.viewTree().forEach(System.out::println);
        treeManagerRepository.viewCategoryTree().forEach(System.out::println);
    }

}
