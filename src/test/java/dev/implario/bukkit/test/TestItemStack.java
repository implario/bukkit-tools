package dev.implario.bukkit.test;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
public class TestItemStack extends ItemStack {

    private final Map<String, Object> nbt;

    public TestItemStack(Material type, int amount, int data, Map<String, Object> nbt) {
        super(type, amount, (short) data);
        this.nbt = nbt;
    }
}
