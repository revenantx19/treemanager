package pro.sky.telegrambot.context;

import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pro.sky.telegrambot.validator.CategoryValidator;

@Slf4j
@Getter
public class MessageContext {

    private final Long chatId;
    @Setter
    private String[] message;
    @Setter
    private Document document;

    private final Update update;

    public MessageContext(Update update, String[] parts) {
        this.message = update.message().document() != null ? new String[]{"upload"} : parts;
        this.chatId = update.message().chat().id();
        this.update = update;
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

    public void setUploadCommand(String command, Document newDocument) {
        document = newDocument;
        message[0] = command;
    }

}
