package clepto.bukkit.gui;

import clepto.bukkit.Lemonade;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class SlotData {

	public static final SlotData EMPTY = new SlotData(null, null,null, null, null);

	@ToString.Include
	private final String nativeStr;
	private final Lemonade base;
	private Itemizer itemizer;

	@Getter
	@ToString.Include
	private final String[] lmbCommands;

	@Getter
	@ToString.Include
	private final String[] rmbCommands;

	@Getter
	@ToString.Include
	private final String info;

	public ItemStack getItem(Player player, GuiContext context, int slotId) {
		if (itemizer != null) return itemizer.toItem(base, player, context, slotId);
		itemizer = Guis.itemizers.get(nativeStr);
		if (itemizer == null) itemizer = Itemizer.STATIC;
		return itemizer.toItem(base, player, context, slotId);
	}

}
