package pro.sky.telegrambot.validator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Валидатор команд и параметров для управления категориями.
 *
 * <p>Этот класс проверяет корректность введенных команд и
 * очищает их от нежелательных символов. Он гарантирует, что
 * команды соответствуют ожидаемому формату и содержат допустимые
 * символы.
 */
@Slf4j
@RequiredArgsConstructor
@Service

public class CategoryValidator {

    private static final List<String> commandsList = List.of("/add", "/del", "/viewTree", "/download", "/upload", "/help");
    private static final String invalidCharsPattern = "[\\\\/:;*?\"<>|^'%`~@]";
    /**
     * Проверяет и очищает вводимую строку команды.
     *
     * <p>Метод разбивает строку на составляющие, проверяет,
     * начинается ли команда с символа '/', существует ли она
     * в списке разрешенных команд и содержит ли недопустимые
     * символы. Возвращает очищенные параметры команды.
     *
     * @param input вводимая строка команды
     * @return массив строк, содержащий команду и её параметры
     * @throws IllegalArgumentException если команда не соответствует требованиям
     */
    public String[] validateAndClean(String input) {

        if (input == null) {
            return null;
        }
        String[] parts = input.split("\\s+", 3);
        if (!parts[0].startsWith("/")) {
            throw new IllegalArgumentException("Команда должна начинаться с '/'");
        }
        if (!commandsList.contains(parts[0])) {
            throw new IllegalArgumentException("Такой команды не существует, введите /help, чтобы ознакомиться со списком всех команд.");
        }
        if ((parts[0].equals("/add") || parts[0].equals("/del")) && parts.length == 1) {
            throw new IllegalArgumentException("Отсутствуют параметры введённой команды");
        }
        parts[0] = parts[0].replace("/","");
        if (parts.length == 1) {
            return parts;
        } else {
            if ((parts.length == 2 || parts.length == 3) && !checkInvalidChars(parts[1])) {
                return parts;
            } else {
                throw new IllegalArgumentException("Имена каталогов содержат запрещенные символы, либо количество параметров больше 2");
            }
        }
    }
    /**
     * Проверяет строку на наличие недопустимых символов.
     *
     * <p>Метод использует регулярное выражение для проверки строки
     * на наличие символов, запрещенных в именах каталогов.
     *
     * @param string строка для проверки
     * @return true, если строка содержит недопустимые символы, иначе false
     */
    public boolean checkInvalidChars(String string) {
        return string.matches(".*" + invalidCharsPattern + ".*");

    }
}
