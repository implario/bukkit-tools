package clepto.bukkit.behaviour;

import clepto.bukkit.B;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@UtilityClass
public class Behaviour {

	public static final Listener eventBehaviour = new Listener() {};

	public static final BehaviourMap<Material>
			useBehaviour = new BehaviourMap<>(Material.class, "use"),
			clickBehaviour = new BehaviourMap<>(Material.class, "click"),
			destroyBehaviour = new BehaviourMap<>(Material.class, "destroy"),
			placeBehaviour = new BehaviourMap<>(Material.class, "place");

	public static final BehaviourMap<EntityType>
			killBehaviour = new BehaviourMap<>(EntityType.class, "kill");

	public static MaterialBehaviour when(Material material) {
		return new MaterialBehaviour(material);
	}

	public static EntityTypeBehaviour when(EntityType type) {
		return new EntityTypeBehaviour(type);
	}

	public static <T extends Event> void on(@DelegatesTo.Target Class<T> type,
														   @DelegatesTo (genericTypeIndex = 0, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		Bukkit.getPluginManager().registerEvent(type, eventBehaviour, EventPriority.NORMAL, (listener, event) -> {
			if (!type.isInstance(event)) return;
			action.setDelegate(event);
			action.call();
		}, B.plugin);
	}


	@RequiredArgsConstructor
	public static class EntityTypeBehaviour {

		private final EntityType entityType;

		public void killed(@DelegatesTo (value = EntityDeathEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			killBehaviour.put(this.entityType, closure);
		}

	}

	@RequiredArgsConstructor
	public static class MaterialBehaviour {

		private final Material material;

		public void used(@DelegatesTo (value = PlayerInteractEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			useBehaviour.put(material, closure);
		}

		public void clicked(@DelegatesTo (value = PlayerInteractEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			clickBehaviour.put(material, closure);
		}

		public void destroyed(@DelegatesTo (value = BlockBreakEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			destroyBehaviour.put(material, closure);
		}

		public void placed(@DelegatesTo (value = BlockPlaceEvent.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
			placeBehaviour.put(material, closure);
		}

	}

}
