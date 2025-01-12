package pro.sky.telegrambot.invoker;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * Инвокер для управления командами Telegram бота.
 *
 * <p>Этот класс отвечает за выполнение команд, регистрируемых в системе.
 * Он хранит команды в мапе и предоставляет метод для их вызова на основе
 * контекста сообщения, получаемого от пользователя.
 */
@Slf4j
@Component
public class Invoker {

    private final Map<String, Command> commandMap = new HashMap<>();
    /**
     * Конструктор для инициализации карты команд.
     *
     * <p>Этот конструктор принимает мапу команд и добавляет их в
     * карту команд для последующего вызова.
     *
     * @param commands мапа команд, зарегистрированных в системе
     */
    public Invoker(Map<String, Command> commands) {
        for (Command command : commands.values()) {
            commandMap.put(command.getNameCommand(), command);
        }
    }
    /**
     * Выполняет команду на основе контекста полученного сообщения.
     *
     * <p>Метод извлекает команду из карты по имени, полученному от сообщения,
     * и выполняет ее, если команда найдена.
     *
     * @param messageContext контекст сообщения, содержащий параметры команды
     */
    public void runCommand(MessageContext messageContext) {
        log.info("Получент контест сообщения" + Arrays.toString(messageContext.getMessage()));
        Command command = commandMap.get(messageContext.getMessage()[0]);
        if (command != null) {
            command.execute(messageContext);
        }
    }
    /**
     * Метод инициализации, который вызывается после создания бина.
     *
     * <p>Этот метод логирует все зарегистрированные команды b
     * для подтверждения их правильной регистрации.
     */
    @PostConstruct
    public void init() {
        for (String key : commandMap.keySet()) {
            log.info("Registered command: {}", key);
        }
    }

}
