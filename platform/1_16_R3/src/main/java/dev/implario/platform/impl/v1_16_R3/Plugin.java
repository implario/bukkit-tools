package dev.implario.platform.impl.v1_16_R3;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.setProperty("dev.implario.bukkit.platformclass", "dev.implario.platform.impl.v1_16_R3.Platform1_16_R3");
    }

}
