package dev.implario.bukkit.test;

import dev.implario.bukkit.item.ItemBuilder;
import dev.implario.bukkit.platform.Platform;
import dev.implario.bukkit.platform.Platforms;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemBuilderTestJava {

    @BeforeAll
    public static void setupPlatform() {
        Platforms.set(new TestPlatform());
    }

    @Test
    public void test() {

        ItemStack item = new ItemBuilder()
                .type(Material.CLAY_BALL)
                .text("Hello\nWorld")
                .amount(64)
                .nbt("sometag", 100)
                .build();

        Map<String, Object> nbt = Platform.get().getNbt(item);
        assertEquals(100, nbt.get("sometag"));

        Map<?, ?> displayTag = (Map<?, ?>) nbt.get("display");

        assertEquals("§fHello", displayTag.get("Name"));
        assertEquals("§fWorld", ((List<?>) displayTag.get("Lore")).get(0));

    }

}
