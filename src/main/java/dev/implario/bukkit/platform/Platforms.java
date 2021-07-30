package dev.implario.bukkit.platform;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Platforms {

    private static Platform platform;
    private static Plugin plugin;

    public static Platform get() {

        if (platform == null) {
            throw new IllegalStateException("Unsupported platform.");
        }

        return platform;

    }

    public static void set(Platform platform) {
        Platforms.platform = platform;
    }

    public static Plugin getPlugin() {
        if (plugin != null) return plugin;
        return plugin = JavaPlugin.getProvidingPlugin(Platforms.class);
    }

}
