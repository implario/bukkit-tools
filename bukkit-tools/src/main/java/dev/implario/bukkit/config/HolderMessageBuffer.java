package dev.implario.bukkit.config;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class HolderMessageBuffer implements IHolderMessage {

    private final String[] lines;

    @Override
    public IHolderMessage set(String key, Object value) {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replace("<" + key + ">", String.valueOf(value));
        }
        return this;
    }

    @Override
    public void forEachLine(Consumer<String> action) {
        for (String line : lines) {
            action.accept(line);
        }
    }

}
