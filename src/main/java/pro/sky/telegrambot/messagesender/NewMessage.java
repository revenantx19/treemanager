package pro.sky.telegrambot.messagesender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
/**
 * Сервис для отправки сообщений и файлов в Telegram.
 *
 * <p>Этот класс предоставляет методы для отправки текстовых сообщений и документов
 * в чаты Telegram с использованием бота. Он использует {@link TelegramBot} для
 * выполнения запросов к Telegram API.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NewMessage {

    private final TelegramBot bot;
    /**
     * Создает и отправляет новое текстовое сообщение в указанный чат.
     *
     * <p>Метод выполняет отправку сообщения с помощью Telegram бота и
     * возвращает текст отправленного сообщения.
     *
     * @param chatId идентификатор чата, куда будет отправлено сообщение
     * @param message текст сообщения для отправки
     * @return текст отправленного сообщения
     */
    public String createNewMessage(Long chatId, String message) {
        bot.execute(new SendMessage(chatId, message));
        return message;
    }
    /**
     * Отправляет файл в указанный чат.
     *
     * <p>Метод выполняет отправку документа с помощью Telegram бота.
     *
     * @param chatId идентификатор чата, куда будет отправлен файл
     * @param file документ, который нужно отправить
     */
    public void sendNewFile(Long chatId, File file) {
        bot.execute(new SendDocument(chatId, file));
    }

}
