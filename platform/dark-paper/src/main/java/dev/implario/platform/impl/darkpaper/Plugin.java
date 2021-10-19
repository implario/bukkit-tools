package dev.implario.platform.impl.darkpaper;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.setProperty("dev.implario.bukkit.platformclass", "dev.implario.platform.impl.darkpaper.PlatformDarkPaper");
    }

}
