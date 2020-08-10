package clepto.bukkit.gui;

import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@ToString(onlyExplicitlyIncluded = true)
public class Gui {

	@ToString.Include
	private final String title;
	private final SlotData[] slots;
	private final int size;
	private final char[] charMap;
	private final char[] unfilledCharMap;
	private final int[] indexMap;

	public Gui(String title, String charMap) {
		this.title = title;
		char[] chars = charMap.toCharArray();
		this.size = (chars.length + 8) / 9 * 9;

		this.charMap = new char[size];
		this.unfilledCharMap = new char[size];
		System.arraycopy(chars, 0, this.charMap, 0, chars.length);
		System.arraycopy(chars, 0, this.unfilledCharMap, 0, chars.length);

		this.slots = new SlotData[size];
		this.indexMap = new int[size];

		Char2IntMap repetitions = new Char2IntArrayMap();

		for (int i = 0; i < size; i++) {
			char c = this.charMap[i];
			int index = repetitions.get(c);
			this.indexMap[i] = index;
			repetitions.put(c, index + 1);
		}
	}

	public void add(char key, SlotData data, boolean fill) {
		for (int i = 0; i < unfilledCharMap.length; i++) {
			if (unfilledCharMap[i] != key) continue;
			slots[i] = data;
			unfilledCharMap[i] = 0;
			if (!fill) return;
		}
	}

	public SlotData getSlotData(int slotId) {
		return slots[slotId];
	}

	public int getIndex(int slotId) {
		return indexMap[slotId];
	}

	public void open(Player player, String guiPayload) {
		Inventory inventory = Bukkit.createInventory(player, size, title);
		ItemStack[] contents = inventory.getContents();

		GuiContext context = GuiEvents.contextMap.get(player);
		context.setOpenedGui(this);
		context.setPayload(guiPayload);

		try {
			for (int slotId = 0; slotId < slots.length; slotId++) {
				SlotData slot = slots[slotId];
				if (slot == null) continue;
				contents[slotId] = slot.getItem(player, context, slotId);
			}

			inventory.setContents(contents);
			player.openInventory(inventory);
		} catch (Exception e) {
			player.closeInventory();
			context.clear();
			e.printStackTrace();
		}

	}

}
