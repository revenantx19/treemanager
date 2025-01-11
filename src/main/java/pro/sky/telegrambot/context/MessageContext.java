package pro.sky.telegrambot.context;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
public class MessageContext {

    private final Long chatId;
    @Setter
    private String[] message;

    public MessageContext(Long chatId) {
        this.chatId = chatId;
        this.message = null;
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
