package clepto.bukkit.item;

import groovy.lang.Closure;
import net.minecraft.server.v1_12_R1.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class Items {

	public static final Map<String, Closure<?>> items = new IdentityHashMap<>();

	public static void register(String name, Closure<?> closure) {
		items.put(name, closure);
	}

	public static ItemStack render(String address) {
		return render(address, null);
	}

	public static ItemStack render(String address, Object context) {
		ItemBuilder builder = new ItemBuilder(context);
		builder.apply(items.get(address));
		return builder.build();
	}

}
