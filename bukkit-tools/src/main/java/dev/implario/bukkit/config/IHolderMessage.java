package dev.implario.bukkit.config;

import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public interface IHolderMessage {

    IHolderMessage set(String key, Object value);

    void forEachLine(Consumer<String> action);

    default void send(CommandSender sender) {
        forEachLine(sender::sendMessage);
    }

}
