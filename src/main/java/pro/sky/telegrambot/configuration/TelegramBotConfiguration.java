package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Конфигурация для инициализации Telegram бота.
 *
 * <p>Этот класс настраивает бота с использованием Spring Framework,
 * загружая токен из файла конфигурации и выполняя начальные команды,
 * такие как удаление всех предыдущих команд.
 */
@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;
    /**
     * Создает экземпляр Telegram бота.
     *
     * <p>Этот метод создает объект {@link TelegramBot} с использованием
     * заранее заданного токена и выполняет команду для удаления всех
     * предыдущих команд бота. Это позволяет начать работу бота с чистого листа.
     *
     * @return экземпляр {@link TelegramBot}
     */
    @Bean
    public TelegramBot telegramBot() {
        TelegramBot bot = new TelegramBot(token);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
