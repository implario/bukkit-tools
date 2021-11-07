package dev.implario.bukkit.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Data
@RequiredArgsConstructor
public class WorldEventFilter implements Predicate<Event> {

    private final List<World> worlds;

    private boolean acceptUnknown = true;

    public WorldEventFilter(World... worlds) {
        this(new ArrayList<>(Arrays.asList(worlds)));
    }

    @Override
    public boolean test(Event event) {
        List<World> worlds = this.worlds;
        // ToDo: PlayerLoginEvent can't be filtered using worlds, probably should add support for it
        if (event instanceof PlayerEvent) return worlds.contains(((PlayerEvent) event).getPlayer().getWorld());
        if (event instanceof EntityEvent) return worlds.contains(((EntityEvent) event).getEntity().getWorld());
        if (event instanceof BlockEvent) return worlds.contains(((BlockEvent) event).getBlock().getWorld());
        if (event instanceof WorldEvent) return worlds.contains(((WorldEvent) event).getWorld());
        if (event instanceof InventoryEvent) return worlds.contains(((InventoryEvent) event).getView().getPlayer().getWorld());
        if (event instanceof InventoryPickupItemEvent)
            return worlds.contains(((InventoryPickupItemEvent) event).getItem().getWorld());
        if (event instanceof VehicleEvent)
            return worlds.contains(((VehicleEvent) event).getVehicle().getWorld());
        if (event instanceof HangingEvent)
            return worlds.contains(((HangingEvent) event).getEntity().getWorld());

        return acceptUnknown;
    }

}
