package clepto.bukkit.event;

import clepto.bukkit.B;
import clepto.bukkit.PlayerSetupEvent;
import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

//		e.deathMessage = "";
//		e.keepInventory = true;
//		wrapper.getPlayer().getInventory().clear();

//      e.setDroppedExp(0);

//		e.setExpToDrop(0);
//		e.setDropItems(false);

@SuppressWarnings ("unchecked")
public class EventAdapter implements Listener {

	private final Plugin plugin;
	private final List<HandlerContext<?>> contexts = new ArrayList<>();

	public EventAdapter(Plugin plugin) {
		this.plugin = plugin;
	}

	public void registerEvents() {

		registerEvent(PlayerQuitEvent.class, (event, pipe, payload) -> pipe.acceptQuit(payload));

		registerEvent(PlayerInteractAtEntityEvent.class, (event, pipe, payload) -> pipe.acceptEntityClick(payload, event.clickedEntity));

		registerEvent(PlayerRespawnEvent.class, (event, pipe, payload) -> {
			pipe.acceptRespawn(payload);
			Location respawn = pipe.getSpawnLocation(payload);
			if (respawn != null) event.setRespawnLocation(respawn);
		});

		registerEvent(PlayerInitialSpawnEvent.class, (event, pipe, payload) -> {
			Location location = pipe.getSpawnLocation(payload);
			if (location != null) event.setSpawnLocation(location);
		});

		registerEvent(PlayerSpawnLocationEvent.class, (event, pipe, payload) -> {
			Location location = pipe.getSpawnLocation(payload);
			if (location != null) event.setSpawnLocation(location);
		});

		registerCancellable(PlayerDeathEvent::getEntity, PlayerDeathEvent.class, (event, pipe, payload) -> {
			event.setDeathMessage(null);
			return pipe.acceptDeath(payload);
		});

		registerCancellable(EntityDamageByEntityEvent::getDamager, EntityDamageByEntityEvent.class, (event, pipe, o) ->
				pipe.acceptDirectDamage(event.damager, event.entity, event.getDamage()));

		registerCancellable(EntityDeathEvent::getEntity, EntityDeathEvent.class, (event, pipe, payload) -> pipe.acceptEntityDeath(event.getEntity()));

		registerEvent(PlayerInteractEvent.class, (event, pipe, payload) -> {
			if (event.getHand() == EquipmentSlot.OFF_HAND) return;
			if (!pipe.acceptClick(payload, event.getAction(), event.getClickedBlock()))
				event.setCancelled(true);
		});

		registerCancellable(EntityDamageEvent::getEntity, EntityDamageEvent.class, (event, pipe, payload) ->
				pipe.acceptIndirectDamage(event.getEntity(), event.getFinalDamage(), event.getCause()));

		registerCancellable(BlockBreakEvent::getPlayer, BlockBreakEvent.class, (event, pipe, payload) ->
				pipe.acceptBlockBreak(payload, event.getBlock()));

		registerCancellable(BlockPlaceEvent::getPlayer, BlockPlaceEvent.class, (event, pipe, payload) ->
				pipe.acceptBlockPlace(payload, event.getBlock(), event.getBlockPlaced()));

		registerCancellable(PlayerAttemptPickupItemEvent.class, (event, pipe, payload) ->
				pipe.acceptPickup(payload, event.getItem(), event.getItem().getItemStack()));

		registerCancellable(PlayerDropItemEvent.class, (event, pipe, payload) ->
				pipe.acceptItemDrop(payload, event.getItemDrop().getItemStack()));

		registerCancellable(InventoryClickEvent::getWhoClicked, InventoryClickEvent.class, (event, pipe, payload) ->
				pipe.acceptInventoryClick(payload, event.action, event.click, event.getCursor(), event.clickedInventory));

		registerEvent(PlayerToggleSneakEvent.class, (event, pipe, payload) ->
				pipe.acceptSneak(payload, event.isSneaking()));

		registerCancellable(PlayerToggleFlightEvent.class, (event, pipe, payload) ->
				pipe.acceptFly(payload, event.isFlying()));

		registerEvent(PlayerSetupEvent.class, (event, pipe, payload) -> pipe.acceptSetup(payload));

		registerEvent(PlayerJoinEvent.class, (event, pipe, payload) -> pipe.acceptJoin(payload));

		registerCancellable(AsyncPlayerChatEvent.class, (event, pipe, payload) -> pipe.acceptChat(payload, event.getMessage()));

		registerEvent(PlayerLoginEvent.class, (event, pipe, payload) -> {
			String kickMessage = pipe.acceptLogin(payload);
			if (kickMessage != null) {
				event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
				event.setKickMessage(kickMessage);
				return;
			}
			Location loc = pipe.getSpawnLocation(payload);
			if (loc != null) event.getPlayer().teleport(loc);
		});

		registerEvent(ProjectileHitEvent::getEntity, ProjectileHitEvent.class, (event, pipe, payload) ->
				pipe.acceptProjectile(event.getEntity(), event.getHitEntity(), event.getHitBlock()));

		Bukkit.getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void onInitialSpawn(PlayerInitialSpawnEvent e) {
		Player player = e.getPlayer();
		for (HandlerContext<?> context : contexts) {
			EventPipe eventPipe = context.getEventPipeBridge().apply(player.getWorld(), player.getUniqueId());
			if (eventPipe == null) continue;
			Object payload = context.getPayloadBridge().apply(player.getUniqueId());
			Location spawnLocation = eventPipe.getSpawnLocation(payload);
			if (spawnLocation != null) e.setSpawnLocation(spawnLocation);
		}
	}

	@EventHandler
	public void onInitialSpawn(PlayerSpawnLocationEvent e) {
		Player player = e.getPlayer();
		for (HandlerContext<?> context : contexts) {
			EventPipe eventPipe = context.getEventPipeBridge().apply(player.getWorld(), player.getUniqueId());
			if (eventPipe == null) continue;
			Object payload = context.getPayloadBridge().apply(player.getUniqueId());
			Location spawnLocation = eventPipe.getSpawnLocation(payload);
			if (spawnLocation != null) e.setSpawnLocation(spawnLocation);
		}
	}

	private <E extends PlayerEvent> void registerEvent(Class<E> type, VoidEventProcessor<E> processor) {
		registerEvent(PlayerEvent::getPlayer, type, processor);
	}

	private <E extends PlayerEvent & Cancellable> void registerCancellable(Class<E> type, BooleanEventProcessor<E> processor) {
		registerCancellable(PlayerEvent::getPlayer, type, processor);
	}

	private <E extends Event & Cancellable> void registerCancellable(Function<E, Entity> entityBridge, Class<E> type, BooleanEventProcessor<E> processor) {
		registerEvent(entityBridge, type, (event, pipe, payload) -> {
			if (!processor.process(event, pipe, payload)) event.setCancelled(true);
		});
	}

	private <E extends Event> void registerEvent(Function<E, Entity> entityBridge, Class<E> type, VoidEventProcessor<E> processor) {
		Bukkit.getPluginManager().registerEvent(type, this, EventPriority.NORMAL, (listener, e) -> {

			if (!type.isInstance(e)) return;

			E event = (E) e;
			Entity entity = entityBridge.apply(event);
			for (HandlerContext<?> context : contexts) {
				try {
					EventPipe<?> eventPipe = context.getEventPipeBridge().apply(entity.getWorld(), entity.getUniqueId());
					if (eventPipe == null) continue;
					Object payload = context.getPayloadBridge().apply(entity.getUniqueId());
					processor.process(event, eventPipe, payload);
				} catch (Exception ex) {
					System.out.println("Failed to process " + type.getName() + " " + processor + " " + entityBridge);
					throw new RuntimeException(ex);
				}
			}
		}, plugin, false);
	}

	public void registerHandlerContext(HandlerContext<?> context) {
		contexts.add(context);
		contexts.sort(Comparator.comparingInt(HandlerContext::getOrder));
	}

	@FunctionalInterface
	public interface BooleanEventProcessor<E extends Event> {

		boolean process(E event, EventPipe pipe, Object payload);

	}

	@FunctionalInterface
	public interface VoidEventProcessor<E extends Event> {

		void process(E event, EventPipe pipe, Object payload);

	}

}
