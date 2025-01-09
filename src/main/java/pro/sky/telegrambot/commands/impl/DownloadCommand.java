package pro.sky.telegrambot.commands.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.commands.Command;
import pro.sky.telegrambot.context.MessageContext;

@Component("download")
@Slf4j
public class DownloadCommand implements Command {
    @Override
    public void execute(MessageContext messageContext) {
        log.info("Скачиваем Excel документ");
    }

    @Override
    public String getNameCommand() {
        return "download";
    }
}
