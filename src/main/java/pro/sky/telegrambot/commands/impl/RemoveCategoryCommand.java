package pro.sky.telegrambot.commands.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;
/**
 * Команда для удаления категории из структуры каталога.
 *
 * <p>Этот класс реализует интерфейс {@link Command} и отвечает за обработку
 * команды удаления каталогов. Он позволяет пользователю удалять как
 * существующие категории по их ID, так и находить и удалять их по имени.
 */
@Slf4j
@RequiredArgsConstructor
@Component("del")
public class RemoveCategoryCommand implements Command {
    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;
    public static boolean flag = false;

    /**
     * Выполняет команду удаления категории.
     *
     * <p>Метод проверяет, является ли первый параметр числовым значением.
     * Если так, он удаляет категорию по ID. Если нет, то ищет категории
     * по имени и предлагает пользователю ввести ID для удаления.
     *
     * @param messageContext контекст сообщения, содержащий параметры команды
     */
    @Override
    @Transactional
    public void execute(MessageContext messageContext) {
        log.info("Запуск метода execute команды del");
        Long chatId = messageContext.getChatId();
        if (flag && messageContext.firstParamIsNumeric()) {
            removeFolderById(Long.valueOf(messageContext.getP1()));
            flag = false;
            log.info(newMessage.createNewMessage(chatId, "Успешно удалён каталог"));
        } else {
            List<String> pathsRemovalCategories = treeManagerRepository.findPathByFolderName(messageContext.getP1());
            if (!pathsRemovalCategories.isEmpty()) {
                String message = String.join("\n", pathsRemovalCategories);
                newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                        "Введите /del и номер каталога, который надо удалить (например: /del 10):\n" + message);
                flag = true;
            } else {
                log.error(newMessage.createNewMessage(chatId, "Каталог с таким именем не найден, либо не предоставлено имя каталога для удаления в предыдущем сообщении"));
            }
        }
    }
    /**
     * Возвращает имя команды.
     *
     * @return имя команды как строка
     */
    @Override
    public String getNameCommand() {
        return "del";
    }
    /**
     * Удаляет категорию по ID.
     *
     * <p>Метод инкапсулирует логику удаления категории из репозитория
     * и логирует процесс удаления.
     *
     * @param id идентификатор категории, которую необходимо удалить
     */
    public void removeFolderById(Long id) {
        log.info("Запуск метода удаления по id");
        treeManagerRepository.removeCategoriesById(id);
    }
}
