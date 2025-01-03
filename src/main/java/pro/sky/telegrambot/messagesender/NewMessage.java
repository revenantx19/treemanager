package pro.sky.telegrambot.messagesender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewMessage {

    private final TelegramBot bot;

    public void createNewMessage(Long chatId, String message) {
        bot.execute(new SendMessage(chatId, message));
    }

}
