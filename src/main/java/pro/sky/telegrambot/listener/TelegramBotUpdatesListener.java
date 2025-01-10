package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.commands.impl.AddCategoryCommand;
import pro.sky.telegrambot.commands.impl.RemoveCategoryCommand;
import pro.sky.telegrambot.commands.impl.ViewTreeCategoryCommand;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.invoker.Invoker;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.validator.CategoryValidator;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final AddCategoryCommand addCategory;
    private final TelegramBot telegramBot;
    private final CategoryValidator categoryValidator;
    private final NewMessage newMessage;
    private final Invoker invoker;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            try {
                String[] parameters = categoryValidator.validateAndClean(update.message().text());

                MessageContext messageContext = new MessageContext(update.message().chat().id(),
                                                                   parameters);
                Long chatId = update.message().chat().id();
                if (messageContext.getCommandName().contains("/add")) {
                    if (messageContext.getMessage().length == 3) {
                        addCategory.addChildFolder(messageContext.getMessage()[1],
                                                   messageContext.getMessage()[2],
                                                   messageContext.getChatId());
                    } else if (messageContext.getMessage().length == 2 && !messageContext.isNumeric()) {
                        addCategory.addRootFolder(messageContext.getMessage()[1], messageContext.getChatId());
                    } else {
                        addCategory.addFolderById(Long.parseLong(messageContext.getMessage()[1]), messageContext.getChatId());
                    }
                }
                if (messageContext.getCommandName().contains("del")) {
                    log.info("Запуск del команды");
                    invoker.runCommand(messageContext);
                }
                if (messageContext.getCommandName().contains("viewTree")) {
                    log.info("Запуск viewTree команды");
                    invoker.runCommand(messageContext);
                }
                if (messageContext.getCommandName().contains("download")) {
                    log.info("Запуск download команды");
                    invoker.runCommand(messageContext);
                }
                if (messageContext.getCommandName().contains("upload")) {
                    log.info("Запуск upload команды");
                    invoker.runCommand(messageContext);
                }

            } catch (IllegalArgumentException e) {
                log.info(newMessage.createNewMessage(chatId, "Каталог с таким именем не найден"));
                e.getMessage();
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //public boolean isNumeric(String str) {
    //    return str.matches("\\d+");
    //}

}
