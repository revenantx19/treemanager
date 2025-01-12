package pro.sky.telegrambot.commands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
/**
 * Команда для показа списка возможных команд.
 *
 * <p>Этот класс реализует интерфейс {@link Command} и отвечает за обработку
 * команды помощи, позволяя пользователю увидеть список всех возможных команд.
 */
@RequiredArgsConstructor
@Component("help")
public class HelpCommand implements Command {

    private final NewMessage newMessage;
    /**
     * Выполняет отправку сообщения пользователю со списком допустимых команд.
     *
     * @param messageContext Контекст сообщения, содержащий информацию о чате и пользователе.
     */
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
                .append("\n7. Загрузить excel документ с деревом категорий: направьте в чат файл с расширением *.xlsx")));
    }
    /**
     * Возвращает имя команды.
     *
     * @return имя команды как строка
     */
    @Override
    public String getNameCommand() {
        return "help";
    }
}
