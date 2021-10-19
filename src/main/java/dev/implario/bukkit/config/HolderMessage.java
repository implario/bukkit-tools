package dev.implario.bukkit.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class HolderMessage implements IHolderMessage {

    private final List<String> lines;

    public HolderMessage(List<String> lines) {
        List<String> list = new ArrayList<>(lines.size());
        this.lines = list;
        for (String line : lines) {
            String replace = line.replace('&', '§');
            list.add(replace);
        }
    }

    @Override
    public IHolderMessage set(String key, Object value) {
        return new HolderMessageBuffer(lines.toArray(new String[0])).set(key, value);
    }

    @Override
    public void forEachLine(Consumer<String> action) {
        lines.forEach(action);
    }
}
