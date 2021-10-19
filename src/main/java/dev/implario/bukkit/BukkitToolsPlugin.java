package dev.implario.bukkit;

import dev.implario.bukkit.platform.Platform;
import dev.implario.bukkit.platform.Platforms;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitToolsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        Platform platform = Platforms.get();
        System.out.println("Using platform " + platform.getClass().getName());

    }
}
