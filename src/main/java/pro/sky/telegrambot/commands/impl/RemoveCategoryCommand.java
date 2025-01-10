package pro.sky.telegrambot.commands.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("del")
public class RemoveCategoryCommand implements Command {
    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;
    private static boolean flag = false;

    @Override
    @Transactional
    public void execute(MessageContext messageContext) {
        log.info("Запуск метода execute команды del");
        Long chatId = messageContext.getChatId();
        if (flag && messageContext.isNumeric()) {
            removeFolderById(Long.valueOf(messageContext.getP1()));
            log.info(newMessage.createNewMessage(chatId, "Успешно удалён каталог"));
            unActivateDeletionFlagById();
        } else {
            List<String> pathsRemovalCategories = treeManagerRepository.findPathByFolderName(messageContext.getP1());
            if (!pathsRemovalCategories.isEmpty()) {
                String message = String.join("\n", pathsRemovalCategories);
                log.info(newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                        "Введите /del и номер каталога, который надо удалить (например: /del 10):\n" + message));
                activateDeletionFlagById();
            } else {
                log.error(newMessage.createNewMessage(chatId, "Каталог с таким именем не найден"));
            }
        }
    }

    @Override
    public String getNameCommand() {
        return "del";
    }

    public void removeFolderById(Long id) {
        log.info("Запуск метода удаления по id");
        treeManagerRepository.removeCategoriesById(id);
    }

    private void activateDeletionFlagById() {
        log.info("Флаг удаления активирован");
        flag = true;
    }

    private void unActivateDeletionFlagById() {
        log.info("Флаг удаления деактивирован");
        flag = false;

    }

}
