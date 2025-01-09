package pro.sky.telegrambot.commands.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Component("viewtree")
public class ViewTreeCategoryCommand implements Command {
    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;

    @Override
    public void execute(MessageContext messageContext) {
        log.info("Вошли в метод execute команды viewTree");
        StringBuilder treeString = new StringBuilder();
        treeManagerRepository.viewCategoryTree()
                .forEach(category -> treeString.append(category).append("\n"));
        newMessage.createNewMessage(messageContext.getChatId(), String.valueOf(treeString));
    }

    public void viewTree(Long chatId) {
        log.info("Вошли в метод viewTree");
        StringBuilder treeString = new StringBuilder();
        treeManagerRepository.viewCategoryTree()
                .forEach(category -> treeString.append(category).append("\n"));
        newMessage.createNewMessage(chatId, String.valueOf(treeString));
    }

    @Override
    public String getNameCommand() {
        return "viewtree";
    }
}
