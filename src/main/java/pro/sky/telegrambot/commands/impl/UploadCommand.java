package pro.sky.telegrambot.commands.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.fileservice.FileSaveService;
import pro.sky.telegrambot.messagesender.NewMessage;
/**
 * Команда для загрузки Excel документа на сервер бота.
 *
 * <p>Этот класс реализует интерфейс {@link Command} и отвечает за
 * обработку команды загрузки файлов. Он получает документ из сообщения,
 * извлекает его содержимое и сохраняет его с помощью {@link FileSaveService}.
 */
@Component("upload")
@Slf4j
@RequiredArgsConstructor
public class UploadCommand implements Command {

    private final FileSaveService fileSaveService;
    private final TelegramBot bot;
    private final NewMessage newMessage;
    /**
     * Выполняет команду загрузки Excel документа.
     *
     * <p>Метод сначала получает документ из сообщения, затем
     * загружает его на сервер и сохраняет данные с помощью
     * {@link FileSaveService}. В случае ошибки, сообщение об ошибке
     * отправляется пользователю.
     *
     * @param messageContext контекст сообщения, содержащий параметры команды
     */
    @Override
    public void execute(MessageContext messageContext) {
        log.info("Загружаем Excel документ");
        GetFileResponse getFileResponse = bot.execute(new GetFile(messageContext.getUpdate().message().document().fileId()));
        File excelFile = getFileResponse.file();
        try {
            fileSaveService.saveDataFromExcel(bot.getFileContent(excelFile));
        } catch (Exception e) {
            log.error(newMessage.createNewMessage(messageContext.getChatId(), e.getMessage()));
        }
    }
    /**
     * Возвращает имя команды.
     *
     * @return имя команды как строка
     */
    @Override
    public String getNameCommand() {
        return "upload";
    }
}
