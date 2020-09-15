package clepto.bukkit.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface EventPipe<T> {

	default void acceptEntityClick(T user, Entity clicked) {}

	default void acceptJoin(T user) {}

	default void acceptQuit(T user) {}

	default void acceptRespawn(T user) {}

	default boolean acceptDeath(T user) {
		return true;
	}

	default boolean acceptEntityDeath(LivingEntity entity) {
		return true;
	}

	default boolean acceptClick(T user, Action action, Block clickedBlock) {
		return true;
	}

	default void acceptProjectile(Projectile projectile, Entity hitEntity, Block hitBlock) {}

	default boolean acceptDirectDamage(Entity attacker, Entity victim, double damage) {
		return true;
	}

	default boolean acceptIndirectDamage(Entity victim, double finalDamage, EntityDamageEvent.DamageCause cause) {
		return true;
	}

	default boolean acceptBlockBreak(T user, Block block) {
		return false;
	}

	default boolean acceptBlockPlace(T user, Block oldBlock, Block newBlock) {
		return false;
	}

	default boolean acceptPickup(T user, Item entity, ItemStack item) {
		return true;
	}

	default boolean acceptItemDrop(T user, ItemStack item) {
		return false;
	}

	default boolean acceptInventoryClick(T user, InventoryAction action, ClickType clickType, ItemStack cursor, Inventory clickedInv) {
		return false;
	}

	default void acceptSneak(T user, boolean enable) {}

	default void acceptSetup(T user) {}

	default boolean acceptFly(T user, boolean enable) {
		return true;
	}

	default String acceptLogin(T user) {
		return null;
	}

	default boolean acceptChat(T user, String message) {
		return true;
	}

	default Location getSpawnLocation(T user) {
		return null;
	}

}
