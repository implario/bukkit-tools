package clepto.bukkit;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class Composer {

	@Getter
	private final ItemStack item = new ItemStack(Material.STONE);

	public Composer composeCore(Consumer<ItemStack> composer) {
		composer.accept(item);
		return this;
	}

}
