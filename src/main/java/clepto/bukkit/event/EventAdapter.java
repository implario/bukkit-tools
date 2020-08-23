package clepto.bukkit.event;

import clepto.bukkit.PlayerSetupEvent;
import clepto.bukkit.event.handler.HandlerContext;
import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
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
	private final List<HandlerContext<?, ?>> contexts = new ArrayList<>();

	public EventAdapter(Plugin plugin) {
		this.plugin = plugin;
	}

	public void registerEvents() {

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

		registerCancellable(PlayerDeathEvent::getEntity, PlayerDeathEvent.class, (event, pipe, payload) -> pipe.acceptDeath(payload));

		registerCancellable(EntityDamageByEntityEvent::getDamager, EntityDamageByEntityEvent.class, (event, pipe, o) ->
				pipe.acceptDirectDamage(event.damager, event.entity, event.getDamage()));

		registerCancellable(EntityDeathEvent::getEntity, EntityDeathEvent.class, (event, pipe, payload) -> pipe.acceptEntityDeath(event.getEntity()));

		registerEvent(PlayerInteractEvent.class, (event, pipe, payload) -> {
			if (event.getHand() == EquipmentSlot.OFF_HAND) return;
			if (!pipe.acceptClick(payload, event.getAction(), event.getClickedBlock()))
				event.setCancelled(true);
		});

		registerCancellable(EntityEvent::getEntity, EntityDamageEvent.class, (event, pipe, payload) ->
				pipe.acceptIndirectDamage(event.getEntity()));

	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptBlockBreak(payload, event.getBlock())) event.setCancelled(true);
		});
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptBlockPlace(payload, event.getBlock(), event.getBlockPlaced())) event.setCancelled(true);
		});
	}

	@EventHandler
	public void onPickup(PlayerAttemptPickupItemEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptItemDrop(payload, event.getItem().getItemStack())) event.setCancelled(true);
		});
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptItemDrop(payload, event.getItemDrop().getItemStack())) event.setCancelled(true);
		});
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity player = event.getWhoClicked();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptInventoryClick(payload, event.getAction(), event.getClick(), event.getCursor(), event.getClickedInventory())) {
				event.setCancelled(true);
			}
		});
	}

	@EventHandler
	public void onToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			pipe.acceptSneak(payload, event.isSneaking());
		});
	}

	@EventHandler
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptFly(payload, event.isFlying())) event.setCancelled(true);
		});
	}

	@EventHandler
	public void onSetup(PlayerSetupEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), EventPipe::acceptSetup);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			if (!pipe.acceptChat(payload, event.getMessage())) event.setCancelled(true);
		});
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile entity = event.getEntity();
		ProjectileSource shooter = entity.getShooter();
		UUID initiatorId = shooter instanceof Entity ? ((Entity) shooter).getUniqueId() : entity.getUniqueId();
		this.process(event, entity.getWorld(), initiatorId, (pipe, payload) -> {
			pipe.acceptProjectile(payload, entity, event.getHitEntity(), event.hitBlock);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), EventPipe::acceptQuit);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), EventPipe::acceptJoin);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {

		Player player = event.getPlayer();
		this.process(event, player.getWorld(), player.getUniqueId(), (pipe, payload) -> {
			String kickReason = pipe.acceptLogin(payload);
			if (kickReason != null) {
				event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
				event.setKickMessage(kickReason);
			}

			Location spawnLocation = pipe.getSpawnLocation(payload);
			if (spawnLocation != null) player.teleport(spawnLocation);

		});

	}

	public <E extends Event> void process(Event e, World world, UUID initiatorId, BiConsumer<EventPipe, Object> executor) {
		for (HandlerContext<?, ?> context : contexts) {
			try {
				EventPipe<?> eventPipe = context.getEventPipe(world, initiatorId);
				if (eventPipe == null) continue;
				Object payload = context.getPayloadBridge().apply(initiatorId);
				executor.accept(eventPipe, payload);
			} catch (Exception ex) {
				Bukkit.getLogger().warning("Failed to process " + e.getEventName() + " on " + initiatorId + " inside " +
						context.getPayloadBridge().getClass().getCanonicalName());
				throw new RuntimeException(ex);
			}
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
			try {
				if (!e.getClass().isAssignableFrom(type)) return;
				E event = (E) e;
				Entity entity = entityBridge.apply(event);
				for (HandlerContext<?, ?> context : contexts) {
					EventPipe<?> eventPipe = context.getEventPipe(entity.getWorld(), entity.getUniqueId());
					if (eventPipe == null) continue;
					Object payload = context.getPayloadBridge().apply(entity.getUniqueId());
					processor.process(event, eventPipe, payload);
				}
			} catch (Exception ex) {
				System.out.println("Failed to process " + type.getName() + " " + processor + " " + entityBridge);
				throw new RuntimeException(ex);
			}
		}, plugin, true);
	}

	public void registerHandlerContext(HandlerContext<?, ?> context) {
		contexts.add(context);
		contexts.sort(Comparator.comparingInt(HandlerContext::getOrder));
	}

}
