package pro.sky.telegrambot.messagesender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewMessage {

    private final TelegramBot bot;

    public String createNewMessage(Long chatId, String message) {
        bot.execute(new SendMessage(chatId, message));
        return message;
    }

    public void sendNewFile(Long chatId, File file) {
        //SendDocument sendDocument = new SendDocument(chatId, file);
        bot.execute(new SendDocument(chatId, file));
    }

}
