package clepto.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DoubleJump implements Listener {

	private final List<Player> airborne = new ArrayList<>();

	public static void init(Plugin plugin) {
		new DoubleJump(plugin);
	}

	private DoubleJump(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			Iterator<Player> iter = airborne.iterator();
			while (iter.hasNext()) {
				Player player = iter.next();
				if (player.isOnGround() || player.getLocation().add(0, -1, 0).getBlock().getType().isSolid()) {
					iter.remove();
					player.setAllowFlight(true);
				}
			}
		}, 1, 1);
	}

	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) return;
		e.setCancelled(true);
		if (airborne.contains(p)) return;
		p.setVelocity(p.getLocation().getDirection().setY(0).normalize().multiply(2.25).setY(1));
		airborne.add(p);
		p.setAllowFlight(false);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		airborne.remove(e.getPlayer());
	}

	@EventHandler
	public void onGameMode(PlayerGameModeChangeEvent e) {
		e.getPlayer().setAllowFlight(!airborne.contains(e.getPlayer()));
	}

}

