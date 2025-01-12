package pro.sky.telegrambot.context;

import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
/**
 * Контекст сообщения, содержащий информацию о сообщении и статусе обновления.
 *
 * <p>Класс хранит информацию о чате и сообщениях, а также предоставляет методы
 * для извлечения и обработки параметров команд. Используется для упрощения работы
 * с обновлениями сообщений в Telegram.
 *
 * @see Update
 * @see Document
 */
@Slf4j
@Getter
public class MessageContext {

    private final Long chatId;
    @Setter
    private String[] message;
    @Setter
    private Document document;

    private final Update update;
    /**
     * Создает объект {@link MessageContext} с указанными обновлениями и параметрами сообщения.
     *
     * <p>Если сообщение содержит документ, первый параметр будет установлен в "upload",
     * иначе будут использованы переданные параметры.
     *
     * @param update объект {@link Update}, содержащий информацию о текущем обновлении
     * @param parts массив строк, представляющий параметры команды
     */
    public MessageContext(Update update, String[] parts) {
        this.message = update.message().document() != null ? new String[]{"upload"} : parts;
        this.chatId = update.message().chat().id();
        this.update = update;
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
