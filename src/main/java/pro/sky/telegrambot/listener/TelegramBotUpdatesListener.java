package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.commands.AddCategory;
import pro.sky.telegrambot.commands.TreeManagerCommands;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TreeManagerRepository treeManagerRepository;
    private final AddCategory addCategory;
    private final TreeManagerCommands treeManagerCommands;
    private final TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                log.info("Processing update: {}", update);
                // ловим отправленное сообщение
                String messageText = update.message().text();
                if (messageText.contains("/add")) {
                    addCategory.addElement(messageText, update);
                }
                if (messageText.contains("/del")) {
                    treeManagerCommands.removeElement(messageText, update);
                }
                if (messageText.contains("/view")) {
                    treeManagerCommands.viewTree();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
