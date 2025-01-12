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

import java.io.InputStream;

@Component("upload")
@Slf4j
@RequiredArgsConstructor
public class UploadCommand implements Command {

    private final FileSaveService fileSaveService;
    private final TelegramBot bot;
    private final NewMessage newMessage;

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

    @Override
    public String getNameCommand() {
        return "upload";
    }
}
