package pro.sky.telegrambot.validator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service

public class CategoryValidator {

    public String[] validateAndClean(String input) {
        String[] parts = input.split("\\s+", 3);
        String invalidCharsPattern = "[\\\\/:;*?\"<>|^'%`~@]";
        if (!parts[0].startsWith("/")) {
            throw new IllegalArgumentException("Команда должна начинаться с '/'");
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
