package clepto.bukkit.behaviour;

import clepto.bukkit.B;
import groovy.lang.Closure;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BehaviourListener implements Listener {

	@EventHandler
	public void handle(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;

		ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
		if (hand != null)
			Behaviour.useBehaviour.call(hand.getType(), event);

		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock != null)
			Behaviour.clickBehaviour.call(clickedBlock.getType(), event);

	}

	@EventHandler
	public void handle(BlockPlaceEvent event) {
		Behaviour.placeBehaviour.call(event.getBlockPlaced().getType(), event);
	}

	@EventHandler
	public void handle(BlockBreakEvent event) {
		Behaviour.destroyBehaviour.call(event.getBlock().getType(), event);
	}

}
