package clepto.bukkit.gui;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class GuiEvents implements Listener {

	protected static final Map<HumanEntity, GuiContext> contextMap = new Reference2ReferenceOpenHashMap<>();

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		contextMap.put(e.getPlayer(), new GuiContext());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		contextMap.remove(e.getPlayer());
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		GuiContext context = contextMap.get(e.getWhoClicked());
		if (context == null || e.getSlot() < 0) return;

		Gui gui = context.getOpenedGui();
		if (gui == null || !context.isOpened()) return;

		e.setCancelled(true);

		int slotId = e.getSlot();
		SlotData slot = gui.getSlotData(slotId);

		if (slot == null) return;

		String[] commands;
		if (e.getClick() == null) return;
		if (e.getClick().isLeftClick()) commands = slot.getLmbCommands();
		else if (e.getClick().isRightClick()) commands = slot.getRmbCommands();
		else return;

		if (commands == null) return;
		String payload = String.valueOf(context.getPayload());
		String index = String.valueOf(gui.getIndex(slotId));

		for (String command : commands) {
			String expression = command
					.replace("<payload>", payload)
					.replace("<index>", index)
					.replace("<info>", String.valueOf(slot.getInfo()))
					.replace("<user>", p.getName());
			if (expression.startsWith("$")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), expression.substring(1));
			else p.performCommand(expression);
		}
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onDrag(InventoryDragEvent e) {
		GuiContext context = contextMap.get(e.getWhoClicked());
		if (context != null && context.getOpenedGui() != null && context.isOpened()) e.setCancelled(true);
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onClose(InventoryCloseEvent e) {
		GuiContext context = contextMap.get(e.getPlayer());
		if (context != null) context.setOpened(false);
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		GuiContext context = contextMap.get(e.getPlayer());
		if (context != null) context.setOpened(true);
	}
}
