package dev.implario.bukkit.platform;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface Platform {

    static Platform get() {
        return Platforms.get();
    }

    ItemStack createItemStack(Material material, int amount, int data, Map<String, Object> nbt);

    Map<String, Object> getNbt(ItemStack itemStack);

}
