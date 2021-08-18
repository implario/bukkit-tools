package clepto.bukkit.item;

import clepto.bukkit.groovy.GroovyUtils;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import lombok.val;
import net.minecraft.server.v1_12_R1.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Items {

	public static final Map<String, Consumer<ItemBuilder>> items = new HashMap<>();

	public static void register(String name,
								@DelegatesTo (value = ItemBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
		items.put(name, GroovyUtils.toConsumer(closure));
	}
	public static void register(String name, Consumer<ItemBuilder> consumer) {
		items.put(name, consumer);
	}

	public static void register(String name, ItemStack itemStack) {
		items.put(name, builder -> {
			org.bukkit.inventory.ItemStack bukkit = itemStack.asBukkitMirror();
			builder.item = bukkit.getType();
			builder.data = itemStack.getData();
			if (bukkit.hasItemMeta()) {
				String displayName = bukkit.getDisplayName();
				if (displayName != null) builder.text.add(displayName);
				List<String> lore = bukkit.getLore();
				if (lore != null) builder.text.addAll(lore);
				Map<String, Object> map = (Map<String, Object>) ItemBuilder.fromNbt(itemStack.tag);
				if (map != null) {
					Object display = map.get("display");
					if (display instanceof Map) {
						((Map<?, ?>) display).remove("Name");
						((Map<?, ?>) display).remove("Lore");
					}
					builder.nbt(map);
				}
			}
		});
	}

	public static ItemStack render(String address) {
		return render(address, address);
	}

	public static ItemStack render(String address, Object context) {
		ItemBuilder builder = new ItemBuilder(context);
		Consumer<ItemBuilder> closure = items.get(address);
		if (closure == null)
			throw new NoSuchElementException("No item '" + address + "'");
		closure.accept(builder);
		return builder.build();
	}

}
