package dev.implario.bukkit.test;

import dev.implario.bukkit.platform.Platform;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class TestPlatform implements Platform {

    @Override
    public ItemStack createItemStack(Material material, int amount, int data, Map<String, Object> nbt) {
        return new TestItemStack(material, amount, data, nbt);
    }

    @Override
    public Map<String, Object> getNbt(ItemStack itemStack) {
        return ((TestItemStack) itemStack).getNbt();
    }
}
