package pro.sky.telegrambot.commands.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
//@Component
public class ViewTreeCategoryCommand {

    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;

    public void viewTree(Long chatId) {
        log.info("Вошли в метод viewTree");
        StringBuilder treeString = new StringBuilder();
        treeManagerRepository.viewCategoryTree()
                .forEach(category -> treeString.append(category).append("\n"));
        newMessage.createNewMessage(chatId, String.valueOf(treeString));
    }
}
