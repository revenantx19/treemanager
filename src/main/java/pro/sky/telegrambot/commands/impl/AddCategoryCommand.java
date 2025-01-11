package pro.sky.telegrambot.commands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Component("add")
public class AddCategoryCommand implements Command {

    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;

    private static boolean flag = false;
    private static String saverChildFolderName = null;

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
    @Override
    public void execute(MessageContext messageContext) {
        log.info("Запуск метода execute команды add");
        Long chatId = messageContext.getChatId();
        List<String> directoriesForAddedFolders = treeManagerRepository.findPathByFolderName(messageContext.getP1());

        try {
            if (flag && messageContext.firstParamIsNumeric()) {
                addChildCategory(messageContext);
            } else {
                addRootOrSelectExistingCategory(messageContext, directoriesForAddedFolders);
            }
        } catch (NoSuchElementException e) {
            log.error(newMessage.createNewMessage(chatId, "Родительская категория не найдена."));
        } catch (NumberFormatException e) {
            log.error(newMessage.createNewMessage(chatId, "Некорректный ID: " + messageContext.getP1()));
        } catch (Exception e) {
            log.error(newMessage.createNewMessage(chatId, "Произошла ошибка: " + e.getMessage()));
        }
    }

    @Override
    public String getNameCommand() {
        return "add";
    }

    private void addChildCategory(MessageContext messageContext) {
        Long folderId = Long.parseLong(messageContext.getP1());
        Long chatId = messageContext.getChatId();
        Optional<Category> parentFolder = treeManagerRepository.findById(folderId);
        if (parentFolder.isPresent()){
            if (!treeManagerRepository.existsByParentIdAndName(parentFolder, saverChildFolderName)) {
                treeManagerRepository.addElement(saverChildFolderName, parentFolder.get().getId());
                log.info(newMessage.createNewMessage(chatId, "Каталог успешно добавлен"));
            } else {
                log.warn(newMessage.createNewMessage(chatId, "Подкаталог уже существует, либо предыдущее сообщение иного формата (/add <folderName>)"));
            }
            unActivateAddFlagById();
        } else {
            throw new NoSuchElementException("Категория с ID " + folderId + " не найдена.");
        }
    }

    private void addRootOrSelectExistingCategory(MessageContext messageContext, List<String> directoriesForAddedFolders) {
        log.info("Запуск метода добавления корневой категории");
        Long chatId = messageContext.getChatId();
        if (messageContext.getMessage().length == 2) {
            if (!treeManagerRepository.existsByNameAndParentIdIsNull(messageContext.getP1())) {
                treeManagerRepository.save(new Category(messageContext.getP1()));
                log.info(newMessage.createNewMessage(chatId, "Корневая категория добавлена: " + messageContext.getP1()));
            } else {
                log.warn(newMessage.createNewMessage(chatId, "Корневая категория уже существует"));
            }
        } else {
            if (!directoriesForAddedFolders.isEmpty()) {
                log.info(newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                        "Введите /add и номер каталога, в который надо добавить (например: /add 10):\n" +
                        String.join("\n", directoriesForAddedFolders)));
                activateAddFlagAndSaveChildName(messageContext.getP2());
            } else {
                log.error(newMessage.createNewMessage(chatId, "Каталогов для добавления не найдено"));
            }
        }
    }

    private void activateAddFlagAndSaveChildName(String p2) {
        saverChildFolderName = p2;
        flag = true;
    }

    private void unActivateAddFlagById() {
        saverChildFolderName = null;
        flag = false;
    }
}
