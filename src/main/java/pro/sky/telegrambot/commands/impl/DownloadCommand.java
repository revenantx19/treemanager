package pro.sky.telegrambot.commands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.fileservice.ExcelExportService;
import pro.sky.telegrambot.fileservice.FileSaveService;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.io.IOException;
import java.util.List;

@Component("download")
@Slf4j
@RequiredArgsConstructor
public class DownloadCommand implements Command {

    private final TreeManagerRepository treeManagerRepository;
    private final NewMessage newMessage;
    private final ExcelExportService excelExportService;
    private final FileSaveService fileSaveService;

    @Override
    public void execute(MessageContext messageContext) {
        log.info("Скачиваем Excel документ");
        List<Category> listOfAllCategories = treeManagerRepository.findAll();
        try {
            newMessage.sendNewFile(messageContext.getUpdate().message().chat().id(),
                    fileSaveService.saveFile(excelExportService.exportCategoriesToExcel(listOfAllCategories)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNameCommand() {
        return "download";
    }
}
