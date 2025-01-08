package pro.sky.telegrambot.commands.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;

@Component("upload")
@Slf4j
public class UploadCommand implements Command {
    @Override
    public void execute() {
        log.info("Загружаем Excel документ");
    }

    @Override
    public String getNameCommand() {
        return "upload";
    }
}
