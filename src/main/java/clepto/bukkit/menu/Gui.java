package clepto.bukkit.menu;

import clepto.bukkit.item.ItemBuilder;
import groovy.lang.Closure;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.*;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class Gui {

	public final Object context;
	private String title;
	private String layout;
	private final List<Button> buildingButtons = new ArrayList<>();
	private Closure[] leftClickMap, rightClickMap;

	public Gui fill(char key, Closure<?> item, Closure<?> onLeftClick, Closure<?> onRightClick) {
		return this.button(key, item, onLeftClick, onRightClick, true);
	}

	public Gui button(char key, Closure<?> item, Closure<?> onLeftClick, Closure<?> onRightClick, boolean fill) {
		this.buildingButtons.add(new Button(key, item, onLeftClick, onRightClick, fill));
		return this;
	}

	public Inventory build() {
		char[] layout = this.layout.replaceAll("\\S", "").toCharArray();
		Inventory inv = Bukkit.createInventory(null, layout.length, title);
		this.leftClickMap = new Closure[layout.length];
		this.rightClickMap = new Closure[layout.length];
		for (Button button : buildingButtons) {
			char key = button.key;
			int index;
			while ((index = Arrays.binarySearch(layout, key)) >= 0) {
				inv.setItem(index, new ItemBuilder(index).apply(button.item).build().asBukkitMirror());
				leftClickMap[index] = button.leftClick;
				rightClickMap[index] = button.rightClick != null ? button.rightClick : button.leftClick;
				layout[index] = 0;
				if (!button.fill) break;
			}
		}
		return inv;
	}

	@Data
	public static class Button {

		private final char key;
		private final Closure<?> item;
		private final Closure<?> leftClick;
		private final Closure<?> rightClick;
		private final boolean fill;

	}

}
