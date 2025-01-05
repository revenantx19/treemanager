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

    /**
     * Описание работы метода addChildFolder:<br>
     * 1. Первоначальный if проверяет существует ли такая связь уже в БД и сколько категорий с таким именем существует,<br>
     * если связь от, если нет, то пишет в чат сообщение об этом.<br>
     * 2. Далее осуществляется запрос в БД для нахождения полного пути каталога и все подходящие варианты
     * помещаются в List<String><br>
     * 3. Второй if проверяет длину List`а и при длине = 1
     * <p>
     * выводятся все полные пути к каталогам + их ID в БД для
     * выбора конкретного каталога, иначе
     *
     * @param parentFolderName - имя каталога родителя
     * @param childFolderName  - имя добавляемого каталога потомка (ребёнка)
     * @param chatId           - id Telegram чата
     */

    public void addChildFolder(String parentFolderName, String childFolderName, Long chatId) {
        saverChildFolderName = childFolderName;
        List<String> directoriesForAddedFolders = treeManagerRepository.findPathByFolderName(parentFolderName);

        if (directoriesForAddedFolders.isEmpty()) {
            log.error("Каталогов для добавления не найдено");
            newMessage.createNewMessage(chatId,"Каталогов для добавления не найдено");
        } else {
            String message = String.join("\n", directoriesForAddedFolders);
            newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                    "Введите /add и номер каталога, в который надо добавить (например: /add 10):\n" + message);
            activateAddingFlagById();
        }

        /*
        if (!treeManagerRepository.existsChildUnderParent(childFolderName, parentFolderName) || directoriesForAddedFolders.size() > 1) {
            if (directoriesForAddedFolders.size() == 1) {
                log.info("Добавляем '{}' в родительскую категорию: '{}'", childFolderName, parentFolderName);
                saverParentFolderEntity = treeManagerRepository.findByName(parentFolderName);
                if (saverParentFolderEntity.isPresent() ) {
                    treeManagerRepository.addElement(childFolderName, saverParentFolderEntity.get().getId());
                    newMessage.createNewMessage(chatId, "Успешно добавлен каталог: '"
                            + childFolderName + "' в родительскую категорию '" + parentFolderName + "'");
                    log.info("Каталог {} успешно добавлен", childFolderName);
                } else {
                    newMessage.createNewMessage(chatId, "Родительская категория не найдена.");
                    log.error("Родительская категория не найдена.");
                }
            } else {
                String message = String.join("\n", directoriesForAddedFolders);
                newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                        "Введите /add и номер каталога, в который надо добавить (например: /add 10):\n" + message);
                activateAddingFlagById();
            }
        } else {
            log.error("Родительская и дочерняя категория уже существуют.");
            newMessage.createNewMessage(chatId, "Родительская и дочерняя категория уже существуют.");
        }

         */

    }

    public void addRootFolder(String rootFolderName, Long chatId) {
        log.info("Вход в метод addRootFolder");
        if (!treeManagerRepository.existsByNameAndParentIdIsNull(rootFolderName)) {
            treeManagerRepository.save(new Category(rootFolderName));
            log.info("Корневая категория добавлена: '{}'", rootFolderName);
            newMessage.createNewMessage(chatId, "Корневая категория добавлена: " + rootFolderName);
        } else {
            newMessage.createNewMessage(chatId, "Корневая категория уже существует");
        }
    }

    public void addFolderById(Long folderId, Long chatId) {
        log.info("Вход в метод addFolderById");
        Optional<Category> parentFolder = treeManagerRepository.findById(folderId);
        //так как добавлять мы может уже в ВЫБИРАЕМУЮ родительскую категорию
        //значит нужно осуществить проверку нет ли там уже добавляемого потомка
        if (flag && !treeManagerRepository.existsByParentIdAndName(parentFolder, saverChildFolderName)) {
            Optional<Category> parentCategory = treeManagerRepository.findById(folderId);
            treeManagerRepository.addElement(saverChildFolderName, parentCategory.get().getId());
            newMessage.createNewMessage(chatId, "Каталог успешно добавлен");
        } else {
            newMessage.createNewMessage(chatId, "Подкаталог уже существует, либо предыдущее сообщение иного формата (/add <folderName>)");
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
