package pro.sky.telegrambot.commands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;

@RequiredArgsConstructor
@Component("help")
public class HelpCommand implements Command {

    private final NewMessage newMessage;

    @Override
    public void execute(MessageContext messageContext) {
        StringBuilder message = new StringBuilder();

        newMessage.createNewMessage(messageContext.getChatId(), String.valueOf(message.append("Допустимые команды:")
                .append("\n1. Добавление корневой категории:\n/add <корневая категория>")
                .append("\n2. Добавление подкатегории в существующую:\n/add <категория> <подкатегория>")
                .append("\n3. Просмотр дерева категорий в структурированном виде: /viewTree")
                .append("\n4. Удаление категории и все подкатегории: /del <категория>")
                .append("\n5. Просмотр всех команд: /help")
                .append("\n6. Скачать excel документ с деревом категорий: /download")
                .append("\n7. Загрузить excel документ с деревом категорий: /upload")));
    }

    @Override
    public String getNameCommand() {
        return "help";
    }
}
