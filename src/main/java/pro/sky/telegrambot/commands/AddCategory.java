package pro.sky.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;
import pro.sky.telegrambot.validator.CategoryValidator;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service

public class AddCategory {

    private final NewMessage newMessage;
    private final TelegramBot bot;
    private final TreeManagerRepository treeManagerRepository;
    private final CategoryValidator categoryValidator;

    public void addElement(String messageText, Update update) {
        //НАДО ОБРАБОТАТЬ ЭТО МЕСТО НА НЕПРАВИЛЬНЫЕ ВВОДЫ И НАЛИЧИЕ НЕКОРРЕКТНЫХ СИМВОЛОВ В СООБЩЕНИИ
        //ОБЪЕКТИВНО ИСПОЛЬЗОВАТЬ РЕГУЛЯРНЫЕ ВЫРАЖЕНИЯ
        //РАЗБИТЬ НА МЕТОДЫ ==2 ==3 и отличные ситуации

        try {
            String[] category = categoryValidator.validateAndClean(messageText).split(" ");
            System.out.println("category.length = " + category.length);
            if (category.length == 2) {
                String parentName = category[0];
                String childName = category[1];
                log.info("Добавляем '{}' в родительскую категорию: '{}'", childName, parentName);
                Optional<Category> parentCategory = treeManagerRepository.findByName(parentName);
                if (parentCategory.isPresent() && !treeManagerRepository.existsName(parentCategory.get().getId(), childName)) {
                    treeManagerRepository.addElement(childName, parentCategory.get().getId());
                    newMessage.createNewMessage(update, "Успешно добавлен каталог: '" + childName + "' в родительскую категорию '" + parentName + "'");
                } else {
                    newMessage.createNewMessage(update, "Родительская категория не найдена или добавляемая категория уже существует.");
                    log.error("Родительская категория не найдена или добавляемая категория уже существует.");
                }
            } else if (category.length == 1 && !treeManagerRepository.existsParentCategory(category[0])) {
                treeManagerRepository.save(new Category(category[0]));
                log.info("Корневая категория добавлена: '{}'", category[0]);
                newMessage.createNewMessage(update, "Корневая категория добавлена: " + category[0]);
            } else {
                log.error("Корневая категория '{}' уже существует", category[0]);
                newMessage.createNewMessage(update, "Корневая категория уже существует.");
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            newMessage.createNewMessage(update, "Имена каталогов содержат недопустимые символы.");
        }
    }


}
