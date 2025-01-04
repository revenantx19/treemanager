package pro.sky.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;
import pro.sky.telegrambot.validator.CategoryValidator;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service

public class AddCategory {

    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;

    private static boolean flag = false;

    public void addChildFolder(String parentFolderName, String childFolderName, Long chatId) {
        //НАДО ОБРАБОТАТЬ ЭТО МЕСТО НА НЕПРАВИЛЬНЫЕ ВВОДЫ И НАЛИЧИЕ НЕКОРРЕКТНЫХ СИМВОЛОВ В СООБЩЕНИИ
        //ОБЪЕКТИВНО ИСПОЛЬЗОВАТЬ РЕГУЛЯРНЫЕ ВЫРАЖЕНИЯ
        //РАЗБИТЬ НА МЕТОДЫ ==2 ==3 и отличные ситуации
        //ЕСЛИ ПАПОК С ОДНИМ НАЗВАНИЕМ БОЛЬШЕ 1, ТО НАДО КАК-ТО ВЫБИРАТЬ В КАКУЮ ДОБАВЛЯТЬ

        log.info("Добавляем '{}' в родительскую категорию: '{}'", childFolderName, parentFolderName);
        Optional<Category> parentCategory = treeManagerRepository.findByName(parentFolderName);
        if (parentCategory.isPresent() && !treeManagerRepository.existsName(parentCategory.get().getId(), childFolderName)) {
            treeManagerRepository.addElement(childFolderName, parentCategory.get().getId());
            newMessage.createNewMessage(chatId, "Успешно добавлен каталог: '"
                    + childFolderName + "' в родительскую категорию '" + parentFolderName + "'");
        } else {
            newMessage.createNewMessage(chatId, "Родительская категория не найдена или добавляемая категория уже существует.");
            log.error("Родительская категория не найдена или добавляемая категория уже существует.");
        }
    }

    public void addRootFolder(String rootFolderName, Long chatId) {
        treeManagerRepository.save(new Category(rootFolderName));
        log.info("Корневая категория добавлена: '{}'", rootFolderName);
        newMessage.createNewMessage(chatId, "Корневая категория добавлена: " + rootFolderName);
    }
}
