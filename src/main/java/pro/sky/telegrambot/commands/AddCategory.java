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

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service

public class AddCategory {

    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;

    private static boolean flag = false;
    private static String saverChildFolderName = null;
    private static Optional<Category> saverParentFolderEntity = null;

    public void addChildFolder(String parentFolderName, String childFolderName, Long chatId) {
        //НАДО ОБРАБОТАТЬ ЭТО МЕСТО НА НЕПРАВИЛЬНЫЕ ВВОДЫ И НАЛИЧИЕ НЕКОРРЕКТНЫХ СИМВОЛОВ В СООБЩЕНИИ
        //ОБЪЕКТИВНО ИСПОЛЬЗОВАТЬ РЕГУЛЯРНЫЕ ВЫРАЖЕНИЯ
        //РАЗБИТЬ НА МЕТОДЫ ==2 ==3 и отличные ситуации
        //ЕСЛИ ПАПОК С ОДНИМ НАЗВАНИЕМ БОЛЬШЕ 1, ТО НАДО КАК-ТО ВЫБИРАТЬ В КАКУЮ ДОБАВЛЯТЬ

        log.info("Проверка существования связи потомок-родитель: " + treeManagerRepository.existsChildUnderParent(childFolderName, parentFolderName));

        if (!treeManagerRepository.existsChildUnderParent(childFolderName, parentFolderName)) {
            List<String> directoriesForAddedFolders = treeManagerRepository.findPathByFolderName(parentFolderName);
            if (directoriesForAddedFolders.size() > 1) {
                String message = String.join("\n", directoriesForAddedFolders);
                saverChildFolderName = childFolderName;
                newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                        "Введите /add и номер каталога, в который надо добавить (например: /add 10):\n" + message);
                activateAddingFlagById();
            } else {
                log.info("Добавляем '{}' в родительскую категорию: '{}'", childFolderName, parentFolderName);
                saverParentFolderEntity = treeManagerRepository.findByName(parentFolderName);
                if (saverParentFolderEntity.isPresent() && !treeManagerRepository.existsName(saverParentFolderEntity.get().getId(), childFolderName)) {
                    treeManagerRepository.addElement(childFolderName, saverParentFolderEntity.get().getId());
                    newMessage.createNewMessage(chatId, "Успешно добавлен каталог: '"
                            + childFolderName + "' в родительскую категорию '" + parentFolderName + "'");
                } else {
                    newMessage.createNewMessage(chatId, "Родительская категория не найдена.");
                    log.error("Родительская категория не найдена.");
                }
            }
        } else {
            log.error("Родительская и дочерняя категория уже существуют.");
            newMessage.createNewMessage(chatId, "Родительская и дочерняя категория уже существуют.");
        }
    }

    public void addRootFolder(String rootFolderName, Long chatId) {
        treeManagerRepository.addElement(saverChildFolderName, saverParentFolderEntity.get().getId());
        log.info("Корневая категория добавлена: '{}'", rootFolderName);
        newMessage.createNewMessage(chatId, "Корневая категория добавлена: " + rootFolderName);
    }

    public void addFolderById(Long folderId, Long chatId) {
        if (flag) {
            Optional<Category> parentCategory = treeManagerRepository.findById(folderId);
            treeManagerRepository.addElement(saverChildFolderName, parentCategory.get().getId());
            newMessage.createNewMessage(chatId, "Каталог успешно добавлен");
        } else {
            newMessage.createNewMessage(chatId, "Для добавления каталога по ID, вначале проверьте существование его в базе (/add <params>)");
        }
        unActivateAddingFlagById();
    }

    private void activateAddingFlagById() {
        flag = true;
    }

    private void unActivateAddingFlagById() {
        flag = false;
    }
}
