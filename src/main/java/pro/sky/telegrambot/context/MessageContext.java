package pro.sky.telegrambot.context;

import com.pengrad.telegrambot.model.Document;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MessageContext {

    private final Long chatId;
    @Setter
    private String[] message;
    @Setter
    private Document document;

    public MessageContext(Long chatId) {
        this.chatId = chatId;
        this.message = new String[]{""};
        this.document = new Document();
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
