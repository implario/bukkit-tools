package clepto.bukkit.gui;

import clepto.bukkit.Lemonade;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface Itemizer {

	ItemStack AIR = new ItemStack();
	Itemizer STATIC = (base, player, context, slotId) -> base == null ? AIR : base.render();

	ItemStack toItem(Lemonade base, Player player, GuiContext context, int slotId);

}
