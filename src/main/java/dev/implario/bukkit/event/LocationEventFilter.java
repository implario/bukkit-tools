package dev.implario.bukkit.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;

import java.util.function.Predicate;

@Data
@RequiredArgsConstructor
public class LocationEventFilter implements Predicate<Event> {

    private final Predicate<Location> matcher;

    private boolean acceptUnknown = true;

    @Override
    public boolean test(Event event) {
        // ToDo: PlayerLoginEvent can't be filtered using worlds, probably should add support for it
        if (event instanceof PlayerEvent) return matcher.test(((PlayerEvent) event).getPlayer().getLocation());
        if (event instanceof EntityEvent) return matcher.test(((EntityEvent) event).getEntity().getLocation());
        if (event instanceof BlockEvent) return matcher.test(((BlockEvent) event).getBlock().getLocation());
        if (event instanceof InventoryEvent) return matcher.test(((InventoryEvent) event).getView().getPlayer().getLocation());
        if (event instanceof InventoryPickupItemEvent)
            return matcher.test(((InventoryPickupItemEvent) event).getItem().getLocation());
        if (event instanceof VehicleEvent)
            return matcher.test(((VehicleEvent) event).getVehicle().getLocation());
        if (event instanceof HangingEvent)
            return matcher.test(((HangingEvent) event).getEntity().getLocation());

        return acceptUnknown;
    }

}
