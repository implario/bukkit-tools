package clepto.bukkit.menu;

import clepto.bukkit.B;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Guis implements Listener {

	public static final Map<String, Closure<?>> guis = new HashMap<>();
	public static final Map<Player, Gui> guiPlayerMap = new HashMap<>();

	public static void register(String name, @DelegatesTo(value = Gui.class, strategy = Closure.DELEGATE_FIRST) Closure closure) {
		guis.put(name, closure);
	}

	public static Gui render(Player player, String address, Object context) {
		Gui gui = new Gui(context);
		Closure<?> closure = guis.get(address);
		if (closure == null)
			throw new NoSuchElementException("No gui '" + address + "'");
		closure.setDelegate(gui);
		closure.call(player);
		return gui;
	}

	public static void open(Player player, String guiAddress, Object context) {
		Gui render = render(player, guiAddress, context);
		player.openInventory(render.build());
		guiPlayerMap.put(player, render);
	}

	public static void init() {
		B.events(new Guis());
	}

	@EventHandler
	public void handle(InventoryClickEvent e) {
		Gui gui = guiPlayerMap.get(e.getWhoClicked());
		if (gui == null) return;
		e.setCancelled(true);
		int rawSlot = e.getRawSlot();
		if (rawSlot < 0 || rawSlot >= gui.leftClickMap().length) return;
		ClickType click = e.getClick();
		if (click == null) return;
		Closure<?> action = null;
		if (click.isLeftClick()) action = gui.leftClickMap()[rawSlot];
		else if (click.isRightClick()) action = gui.rightClickMap()[rawSlot];
		if (action == null) return;
		action.setDelegate(e.getWhoClicked());
		action.call();
	}

	@EventHandler
	public void handle(InventoryCloseEvent e) {
		HumanEntity player = e.getPlayer();
		if (player instanceof Player) Guis.guiPlayerMap.remove(player);
	}

	@EventHandler
	public void handle(PlayerQuitEvent e) {
	    guiPlayerMap.remove(e.getPlayer());
	}



	public static Gui.Button staticButton(String key) {
		return new Gui.Button(key.charAt(0));
	}

}
