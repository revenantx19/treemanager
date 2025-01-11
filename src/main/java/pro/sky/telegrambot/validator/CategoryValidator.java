package pro.sky.telegrambot.validator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service

public class CategoryValidator {

    private static final List<String> commandsList = List.of("/add", "/del", "/viewTree", "/download", "/upload", "/help");

    public String[] validateAndClean(String input) {
        String[] parts = input.split("\\s+", 3);
        String invalidCharsPattern = "[\\\\/:;*?\"<>|^'%`~@]";
        if (!parts[0].startsWith("/")) {
            throw new IllegalArgumentException("Команда должна начинаться с '/'");
        }
        if (!commandsList.contains(parts[0])) {
            throw new IllegalArgumentException("Такой команды не существует, введите /help, чтобы ознакомиться со списком всех команд.");
        }

        parts[0] = parts[0].replace("/","");
        if (parts.length == 1) {
            return parts;
        } else {
            if ((parts.length == 2 || parts.length == 3) && !parts[1].matches(".*" + invalidCharsPattern + ".*")) {
                return parts; //Arrays.copyOfRange(parts, 1, parts.length);
            } else {
                throw new IllegalArgumentException("Имена каталогов содержат запрещенные символы, либо количество параметров больше 2");
            }
        }
    }

}
