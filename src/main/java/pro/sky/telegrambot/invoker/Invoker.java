package pro.sky.telegrambot.invoker;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class Invoker {

    private final Map<String, Command> commandMap = new HashMap<>();

    public Invoker(Map<String, Command> commands) {
        for (Command command : commands.values()) {
            commandMap.put(command.getNameCommand(), command);
        }
    }

    public void runCommand(MessageContext messageContext) {
        log.info("Получент контест сообщения" + Arrays.toString(messageContext.getMessage()));
        Command command = commandMap.get(messageContext.getMessage()[0]);
        if (command != null) {
            command.execute(messageContext);
        }
    }


    @PostConstruct
    public void init() {
        for (String key : commandMap.keySet()) {
            log.info("Registered command: {}", key);
        }
    }

}
