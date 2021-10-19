package dev.implario.bukkit.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Platforms {

    private static Platform platform;
    private static Plugin plugin;
    private static boolean triedAutoDiscovery;

    public static Platform get() {

        if (platform == null) {

            if (!triedAutoDiscovery) {
                tryAutoDiscovery();
                triedAutoDiscovery = true;
                if (platform != null) {
                    return platform;
                }
            }

            throw new IllegalStateException("No plugin providing the platform found.");
        }

        return platform;

    }

    public static void tryAutoDiscovery() {


        String className = System.getProperty("dev.implario.bukkit.platformclass");
        if (className == null) {
            System.out.println("Unable to use auto-discovery to detect the platform");
            return;
        }

        try {
            Class<?> platformClass = Class.forName(className);
            Platforms.platform = (Platform) platformClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while loading dev.implario.bukkit.platformClass: ", exception);
            exception.printStackTrace();
        }

    }

    public static void set(Platform platform) {
        Platforms.platform = platform;
    }

    public static Plugin getPlugin() {
        if (plugin != null) return plugin;
        return plugin = JavaPlugin.getProvidingPlugin(Platforms.class);
    }

}
