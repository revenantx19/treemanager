package pro.sky.telegrambot.commands.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class RemoveCategoryCommand {

    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;

    private static boolean flag = false;

    @Transactional
    public void findAllFoldersAndRemoveIfFolderIsUnique(String params, Long chatId) {
        List<String> pathsRemovalCategories = treeManagerRepository.findPathByFolderName(params);
        if (pathsRemovalCategories.size() > 1) {
            String message = String.join("\n", pathsRemovalCategories);
            newMessage.createNewMessage(chatId, "Найдены следующие каталоги.\n" +
                    "Введите /del и номер каталога, который надо удалить (например: /del 10):\n" + message);
            activateDeletionFlagById();
        } else {
            if (treeManagerRepository.existsByFolderName(params)) {
                removeFolderByName(params);
                newMessage.createNewMessage(chatId, "Каталог успешно удалён");
            } else {
                newMessage.createNewMessage(chatId, "Каталог с таким именем не найден");
            }
        }
    }

    @Transactional
    public void removeFolderById(Long id, Long chatId) {
        log.info("Вошли в метод removeFolderById");
        if (flag) {
            removeFolderById(id);
            newMessage.createNewMessage(chatId, "Успешно удалён каталог");
        } else {
            newMessage.createNewMessage(chatId, "Для удаления каталога, вначале проверьте существование его в базе (/del folder)");
        }
        unActivateDeletionFlagById();
    }

    public void removeFolderByName(String params) {
        treeManagerRepository.removeByName(params);
    }

    public void removeFolderById(Long id) {
        treeManagerRepository.removeCategoriesById(id);
    }

    private void activateDeletionFlagById() {
        flag = true;
    }

    private void unActivateDeletionFlagById() {
        flag = false;
    }

}
