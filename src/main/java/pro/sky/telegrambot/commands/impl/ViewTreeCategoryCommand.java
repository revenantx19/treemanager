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
/**
 * Команда для отображения структуры категорий в виде дерева.
 *
 * <p>Этот класс реализует интерфейс {@link Command} и отвечает за
 * обработку команды для отображения дерева категорий. Он извлекает
 * информацию о категориях из {@link TreeManagerRepository} и
 * формирует сообщение, которое отправляется пользователю.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Component("viewTree")
public class ViewTreeCategoryCommand implements Command {
    private final NewMessage newMessage;
    private final TreeManagerRepository treeManagerRepository;
    /**
     * Выполняет команду для отображения структуры категорий.
     *
     * <p>Метод извлекает список категорий и формирует строку с
     * информацией. Если список пуст, отправляется сообщение "Таблица пуста".
     *
     * @param messageContext контекст сообщения, содержащий параметры команды
     */
    @Override
    public void execute(MessageContext messageContext) {
        log.info("Вошли в метод execute команды viewTree");
        List<String> treeList = treeManagerRepository.viewCategoryTree();
        String treeString = treeList.isEmpty() ? "Таблица пуста" : String.join("\n", treeList);
        newMessage.createNewMessage(messageContext.getChatId(), treeString);
    }
    /**
     * Возвращает имя команды.
     *
     * @return имя команды как строка
     */
    @Override
    public String getNameCommand() {
        return "viewTree";
    }
}
