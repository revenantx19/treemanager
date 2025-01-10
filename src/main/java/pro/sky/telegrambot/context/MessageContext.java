package pro.sky.telegrambot.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
@Getter
public class MessageContext {

    private final Long chatId;
    private final String[] message;

    public MessageContext(Long chatId, String[] message) {
        this.chatId = chatId;
        this.message = message;
    }

    public String getCommandName() {
        return message[0];
    }

    public String getP1() {
        return message[1];
    }

    public String getP2() {
        return message[2];
    }

    public boolean firstParamIsNumeric() {
        return getP1().matches("\\d+");
    }

}
