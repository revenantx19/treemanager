package pro.sky.telegrambot.commands.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Component("viewTree")
public class ViewTreeCategoryCommand implements Command {
    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;

    @Override
    public void execute(MessageContext messageContext) {
        log.info("Вошли в метод execute команды viewTree");
        List<String> treeList = treeManagerRepository.viewCategoryTree();
        String treeString = treeList.isEmpty() ? "Таблица пуста" : String.join("\n", treeList);
        newMessage.createNewMessage(messageContext.getChatId(), treeString);
    }

    @Override
    public String getNameCommand() {
        return "viewTree";
    }
}
