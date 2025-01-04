package pro.sky.telegrambot.commands;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewTreeCategory {

    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;

    public void viewTree(Long chatId) {
        log.info("Вошли в метод viewTree");
        StringBuilder treeString = new StringBuilder();
        treeManagerRepository.viewCategoryTree()
                .forEach(category -> treeString.append(category).append("\n"));
        newMessage.createNewMessage(chatId, String.valueOf(treeString));
    }
}
