package clepto.bukkit.event;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.world.WorldEvent;

import java.util.function.Predicate;

public class EventFilters {

	public static Predicate<Event> byWorld(World world) {
		return event -> {
			if (event instanceof PlayerEvent) return ((PlayerEvent) event).getPlayer().getWorld() == world;
			if (event instanceof EntityEvent) return ((EntityEvent) event).getEntity().getWorld() == world;
			if (event instanceof BlockEvent) return ((BlockEvent) event).getBlock().getWorld() == world;
			if (event instanceof WorldEvent) return ((WorldEvent) event).getWorld() == world;
			if (event instanceof InventoryEvent) return ((InventoryEvent) event).getView().getPlayer().getWorld() == world;
			if (event instanceof InventoryPickupItemEvent)
				return ((InventoryPickupItemEvent) event).getItem().getWorld() == world;
			if (event instanceof VehicleEvent)
				return ((VehicleEvent) event).getVehicle().getWorld() == world;
			if (event instanceof HangingEvent)
				return ((HangingEvent) event).getEntity().getWorld() == world;
			return false;
		};
	}

}
