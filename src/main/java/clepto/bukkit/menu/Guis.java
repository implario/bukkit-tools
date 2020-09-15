package clepto.bukkit.menu;

import clepto.bukkit.B;
import groovy.lang.Closure;
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

	public static void register(String name, Closure<?> closure) {
		guis.put(name, closure);
	}

	public static Gui render(String address) {
		return render(address, address);
	}

	public static Gui render(String address, Object context) {
		Gui gui = new Gui(context);
		Closure<?> closure = guis.get(address);
		if (closure == null)
			throw new NoSuchElementException("No gui '" + address + "'");
		closure.setDelegate(gui);
		closure.call(context);
		return gui;
	}

	public static void open(Player player, String guiAddress, Object context) {
		Gui render = render(guiAddress, context);
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
		if (click.isLeftClick()) gui.leftClickMap()[rawSlot].call();
		else if (click.isRightClick()) gui.rightClickMap()[rawSlot].call();
	}

	@EventHandler
	public void handle(InventoryCloseEvent e) {
		Guis.guiPlayerMap.remove(e.getPlayer());
	}

	@EventHandler
	public void handle(PlayerQuitEvent e) {
	    guiPlayerMap.remove(e.getPlayer());
	}

}
