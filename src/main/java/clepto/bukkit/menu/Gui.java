package clepto.bukkit.menu;

import clepto.bukkit.item.ItemBuilder;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class Gui {

	public static ItemStack defaultItem;

	public final String address;
	public final Object context;
	public String title = "Меню";
	public String layout;
	public final List<Button> buildingButtons = new ArrayList<>();
	private Closure[] leftClickMap, rightClickMap;

	public Button button(String key) {
		return this.button(key.charAt(0));
	}

	public Button button(char key) {
		Button button = new Button(key);
		this.buildingButtons.add(button);
		return button;
	}

	public void button(Button button) {
		this.buildingButtons.add(button);
	}

	public Inventory build() {
		if (this.layout == null) throw new IllegalStateException("No layout specified!");
		char[] layout = this.layout.replaceAll("\\s", "").toCharArray();
		Inventory inv = Bukkit.createInventory(null, layout.length, title);
		this.leftClickMap = new Closure[layout.length];
		this.rightClickMap = new Closure[layout.length];
		for (Button button : buildingButtons) {
			char key = button.key;
			if (key == 0)
				throw new IllegalArgumentException("\\0 key is not supported");

			for (int i = 0; i < layout.length; i++) {
				if (layout[i] != key) continue;
				ItemStack item = button.icon == null ? defaultItem : button.icon;
				layout[i] = 0;
				if (item == null) continue;
				inv.setItem(i, item.asBukkitMirror());
				leftClickMap[i] = button.leftClick;
				rightClickMap[i] = button.rightClick != null ? button.rightClick : button.leftClick;
				if (!button.fill) break;
			}
		}
		return inv;
	}

	@Data
	public static class Button {

		private final char key;
		private ItemStack icon;
		private Closure<?> leftClick;
		private Closure<?> rightClick;
		private boolean fill;

		@Tolerate
		public Button(String key) {
			this(key.charAt(0));
		}

		public Button fillAvailable() {
			this.fill = true;
			return this;
		}

		@Tolerate
		public Button icon(@DelegatesTo (value = ItemBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure iconClosure) {
			this.icon = new ItemBuilder().apply(iconClosure).build();
			return this;
		}

		public Button leftClick(@DelegatesTo (value = Player.class, strategy = Closure.DELEGATE_FIRST) Closure leftClickClosure) {
			this.leftClick = leftClickClosure;
			return this;
		}

		public Button rightClick(@DelegatesTo (value = Player.class, strategy = Closure.DELEGATE_FIRST) Closure rightClickClosure) {
			this.rightClick = rightClickClosure;
			return this;
		}

	}

}
