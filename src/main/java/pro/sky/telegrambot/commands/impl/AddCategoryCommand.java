package pro.sky.telegrambot.commands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Команда для добавления категории в структуру каталога.
 *
 * <p>Этот класс реализует интерфейс {@link Command} и отвечает за обработку
 * команды добавления категории. Он может добавлять как корневые, так и дочерние категории
 * в зависимости от переданных параметров.
 *
 * <p>Включает в себя методы для добавления категорий и обработки ошибок
 * при выполнении команд.
 */
@RequiredArgsConstructor
@Slf4j
@Component("add")
public class AddCategoryCommand implements Command {

    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;

    public static String saverChildFolderName = null;

    /**
     * Выполняет команду добавления категории.
     *
     * <p>Метод проверяет контекст сообщения и определяет, должна ли
     * категория быть корневой или дочерней, а затем вызывает
     * соответствующий метод для добавления.
     *
     * @param messageContext контекст сообщения, содержащий параметры команды
     */
    @Override
    public void execute(MessageContext messageContext) {
        log.info("Запуск метода execute команды add");
        Long chatId = messageContext.getChatId();
        List<String> directoriesForAddedFolders = treeManagerRepository.findPathByFolderName(messageContext.getP1());
        try {
            if (messageContext.firstParamIsNumeric()) {
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

    /**
     * Возвращает имя команды.
     *
     * @return имя команды как строка
     */
    @Override
    public String getNameCommand() {
        return "add";
    }

    /**
     * Добавляет дочернюю категорию.
     *
     * <p>Метод принимает контекст сообщения и добавляет новую категорию
     * в указанной родительской категории, если такая категория существует.
     *
     * @param messageContext контекст сообщения
     * @throws NoSuchElementException если указанная родительская категория не найдена
     */
    public void addChildCategory(MessageContext messageContext) {
        log.info("Запуск метода addChildCategory");
        Long chatId = messageContext.getChatId();
        Long folderId = Long.parseLong(messageContext.getP1());
        Optional<Category> parentFolder = treeManagerRepository.findById(folderId);
        if (parentFolder.isPresent()) {
            if (saverChildFolderName != null && !treeManagerRepository.existsByParentIdAndName(parentFolder, saverChildFolderName)) {
                log.info(saverChildFolderName);
                treeManagerRepository.addElement(saverChildFolderName, parentFolder.get().getId());
                saverChildFolderName = null;
                log.info(newMessage.createNewMessage(chatId, "Каталог успешно добавлен"));
            } else {
                log.warn(newMessage.createNewMessage(chatId, "Подкаталог уже существует, либо предыдущее сообщение не содержит имени подкаталога для добавления"));
            }
        } else {
            throw new NoSuchElementException("Категория с ID " + folderId + " не найдена.");
        }
    }

    /**
     * Добавляет корневую категорию или выбирает существующую категорию.
     *
     * <p>Метод проверяет параметры и, если необходимо, запрашивает
     * у пользователя дополнительные действия для добавления каталогов.
     *
     * @param messageContext             контекст сообщения
     * @param directoriesForAddedFolders список существующих каталогов
     */
    public void addRootOrSelectExistingCategory(MessageContext messageContext, List<String> directoriesForAddedFolders) {
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
                saverChildFolderName = messageContext.getP2();
            } else {
                log.error(newMessage.createNewMessage(chatId, "Каталогов для добавления не найдено"));
            }
        }
    }


}
