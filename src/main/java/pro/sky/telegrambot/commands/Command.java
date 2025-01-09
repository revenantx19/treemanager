package pro.sky.telegrambot.commands;

import pro.sky.telegrambot.context.MessageContext;

public interface Command {
    void execute(MessageContext messageContext);
    String getNameCommand();
}
