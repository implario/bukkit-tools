package clepto.bukkit.item;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import net.minecraft.server.v1_12_R1.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Items {

	public static final Map<String, Closure<?>> items = new HashMap<>();

	public static void register(String name,
								@DelegatesTo (value = ItemBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		items.put(name, closure);
	}

	public static ItemStack render(String address) {
		return render(address, address);
	}

	public static ItemStack render(String address, Object context) {
		ItemBuilder builder = new ItemBuilder(context);
		Closure<?> closure = items.get(address);
		if (closure == null)
			throw new NoSuchElementException("No item '" + address + "'");
		builder.apply(closure);
		return builder.build();
	}

}
