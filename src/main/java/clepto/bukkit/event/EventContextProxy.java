package clepto.bukkit.event;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

public interface EventContextProxy {

	EventContext getEventContext();

	default <T extends Event> void on(Class<T> type,
									  EventPriority priority,
									  Consumer<T> action) {
		getEventContext().on(type, priority, action);
	}

	default <T extends Event> void on(Class<T> type,
									  Consumer<T> action) {
		this.on(type, EventPriority.NORMAL, action);
	}

	default <T extends Event> void on(@DelegatesTo.Target Class<T> type,
									  @DelegatesTo (genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		on(type, EventPriority.NORMAL, action);
	}

	default <T extends Event> void after(@DelegatesTo.Target Class<T> type,
										 @DelegatesTo (genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		on(type, EventPriority.HIGHEST, action);
	}

	default <T extends Event> void before(@DelegatesTo.Target Class<T> type,
										  @DelegatesTo (genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		on(type, EventPriority.LOWEST, action);
	}

	default <T extends Event> void on(@DelegatesTo.Target Class<T> type,
									  EventPriority priority,
									  @DelegatesTo (genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		getEventContext().on(type, priority, event -> {
			action.setDelegate(event);
			action.call();
		});
	}

	default MaterialContext on(Material material) {
		return on(material, EventPriority.NORMAL);
	}

	default MaterialContext before(Material material) {
		return on(material, EventPriority.LOWEST);
	}

	default MaterialContext after(Material material) {
		return on(material, EventPriority.HIGHEST);
	}

	default MaterialContext on(Material material, EventPriority priority) {
		return new MaterialContext(this, material, priority);
	}

	@RequiredArgsConstructor
	class MaterialContext {

		private final EventContextProxy manager;
		private final Material material;
		private final EventPriority priority;

		public void use(@DelegatesTo (value = PlayerInteractEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			use(event -> {
				closure.setDelegate(event);
				closure.run();
			});
		}

		public void use(Consumer<? super PlayerInteractEvent> action) {
			manager.on(PlayerInteractEvent.class, priority, event -> {
				if (event.getHand() == OFF_HAND || event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) return;
				ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
				if (hand == null || hand.getType() != material) return;
				action.accept(event);
			});
		}

		public void click(@DelegatesTo (value = PlayerInteractEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			click(event -> {
				closure.setDelegate(event);
				closure.run();
			});
		}

		public void click(Consumer<? super PlayerInteractEvent> action) {
			manager.on(PlayerInteractEvent.class, priority, event -> {
				if (event.getHand() == OFF_HAND || event.getAction() != RIGHT_CLICK_BLOCK) return;
				if (event.getClickedBlock().getType() != material) return;
				action.accept(event);
			});
		}

		public void destroy(@DelegatesTo (value = BlockBreakEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			destroy(event -> {
				closure.setDelegate(event);
				closure.run();
			});
		}

		public void destroy(Consumer<? super BlockBreakEvent> action) {
			manager.on(BlockBreakEvent.class, priority, event -> {
				if (event.getBlock().getType() == material) action.accept(event);
			});
		}

		public void place(@DelegatesTo (value = BlockPlaceEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			place(event -> {
				closure.setDelegate(event);
				closure.run();
			});
		}

		public void place(Consumer<? super BlockPlaceEvent> action) {
			manager.on(BlockPlaceEvent.class, priority, event -> {
				if (event.getBlockPlaced().getType() == material) action.accept(event);
			});
		}

	}

	default EntityContext on(EntityType entityType) {
		return on(entityType, EventPriority.NORMAL);
	}

	default EntityContext before(EntityType entityType) {
		return on(entityType, EventPriority.LOWEST);
	}

	default EntityContext after(EntityType entityType) {
		return on(entityType, EventPriority.HIGHEST);
	}

	default EntityContext on(EntityType material, EventPriority priority) {
		return new EntityContext(this, material, priority);
	}

	@RequiredArgsConstructor
	class EntityContext {

		private final EventContextProxy manager;
		private final EntityType entityType;
		private final EventPriority priority;

		public void killed(@DelegatesTo (value = EntityDeathEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			manager.on(EntityDeathEvent.class, priority, event -> {
				if (event.getEntityType() != entityType) return;
				closure.setDelegate(event);
				closure.run();
			});
		}

	}

}
