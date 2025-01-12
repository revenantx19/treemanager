package pro.sky.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Главный класс приложения TelegramBot.
 *
 * <p>Этот класс является точкой входа в приложение, использующее
 * Spring Boot. Он запускает приложение и включает поддержку
 * планирования задач с помощью аннотации {@link EnableScheduling}.
 */
@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {
	/**
	 * Точка входа в приложение.
	 *
	 * <p>Метод запускает Spring Boot приложение, инициируя
	 * его контекст и конфигурацию.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);

	}

}
