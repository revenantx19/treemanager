package pro.sky.telegrambot.validator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service

public class CategoryValidator {

    public String validateAndClean(String input) {
        // Разделение строки на основе команды и параметров
        String[] parts = input.split("\\s+", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Команда должна начинаться с /<команда> и содержать параметры.");
        }
        // Имена каталогов
        String catalogNames = parts[1];
        System.out.println("catalogNames = " + catalogNames);

        // Проверка на запрещенные символы
        String invalidCharsPattern = "[\\\\/:;*?\"<>|^']";
        if (catalogNames.matches(".*" + invalidCharsPattern + ".*")) {
            throw new IllegalArgumentException("Имена каталогов содержат запрещенные символы.");
        }

        // Устранение лишних пробелов и формирование новой строки
        String cleanedCatalogNames = catalogNames.replaceAll("\\s+", " ");

        return cleanedCatalogNames;
    }

}
