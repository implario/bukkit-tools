package clepto.bukkit;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class Composer {

	@Getter
	private final ItemStack item = new ItemStack(Material.STONE);
	private ItemMeta meta = item.getItemMeta();

	public Composer composeCore(Consumer<ItemStack> composer) {
		composer.accept(item);
		meta = item.getItemMeta();
		return this;
	}

	public Composer composeMeta(Consumer<ItemMeta> composer) {
		composer.accept(meta);
		item.setItemMeta(meta);
		return this;
	}


}
