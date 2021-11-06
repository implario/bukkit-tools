package dev.implario.bukkit.entity;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class StandHelper {

    private final ArmorStand stand;
    private final JavaPlugin plugin;

    public StandHelper(@NonNull Location location) {
        stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        plugin = JavaPlugin.getProvidingPlugin(StandHelper.class);
    }

    public StandHelper invisible(boolean state) {
        stand.setVisible(state);
        return this;
    }

    public StandHelper name(String title) {
        stand.setCustomNameVisible(true);
        stand.setCustomName(title == null ? "" : title);
        return this;
    }

    public StandHelper gravity(boolean state) {
        stand.setGravity(state);
        return this;
    }

    public StandHelper marker(boolean state) {
        stand.setMarker(state);
        return this;
    }

    public StandHelper child(boolean state) {
        stand.setSmall(state);
        return this;
    }

    public <T> StandHelper fixedData(@NonNull String key, @NonNull T object) {
        stand.setMetadata(key, new FixedMetadataValue(plugin, object));
        return this;
    }

    // todo: stands
   /* private StandHelper slot(EnumItemSlot slot, @NonNull ItemStack item) {
        stand.getHandle().setSlot(slot, CraftItemStack.asNMSCopy(item));
        return this;
    }*/

    public ArmorStand build() {
        return stand;
    }
}