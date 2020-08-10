package clepto.bukkit.event;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public interface PlayerWrapper {

    CraftPlayer getPlayer();

	default Location getSpawnLocation() {
	    return null;
    }

	@SuppressWarnings ("rawtypes")
	default EventPipe getEventPipe() {
	    return null;
    }

    default boolean hasEventPipe() {
    	return getEventPipe() != null;
	}

	default void setup() {}

    default String getName() {
    	return getPlayer().getName();
	}

    default UUID getUuid() {
    	return getPlayer().getUniqueId();
	}

	default void msg(String message) {
    	getPlayer().sendMessage(message);
	}

	default String getDisplayName() {
    	return getName();
	}

	default Location getLocation() {
		return getPlayer().getLocation();
	}

	default Location getEyes() {
		return getPlayer().getEyeLocation();
	}

	default CraftWorld getWorld() {
		return (CraftWorld) getPlayer().getWorld();
	}

	default void respawn() {
    	getPlayer().spigot().respawn();
	}

	default GameMode getGameMode() {
    	return getPlayer().getGameMode();
	}

	default PlayerInventory getInventory() {
    	return getPlayer().getInventory();
	}

	default boolean isCreative() {
    	return getGameMode() == GameMode.CREATIVE;
	}

	default void closeInventory() {
    	getPlayer().closeInventory();
	}

	default void potion(PotionEffectType effect, int seconds, int amplifier) {
		getPlayer().removePotionEffect(effect);
		getPlayer().addPotionEffect(new PotionEffect(effect, seconds * 20, amplifier));
	}

	default void sendTitle(String title, String subtitle) {
    	getPlayer().sendTitle(title, subtitle);
	}

	default void teleport(Location location) {
    	getPlayer().teleport(location);
	}

	default double getHealth() {
		return getPlayer().getHealth();
	}

	default void performCommand(String command) {
    	getPlayer().performCommand(command);
	}

	default void openInventory(Inventory inventory) {
    	getPlayer().openInventory(inventory);
	}

	default void sendPacket(Packet<?> packet) {
		getPlayer().getHandle().playerConnection.sendPacket(packet);
	}

	default void sendMessage(String... messages) {
		getPlayer().sendMessage(messages);
	}

}
