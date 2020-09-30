package clepto.bukkit.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import java.util.Arrays;

public class DirtyCommandManager implements CommandManager {

	@Override
	public Command registerCommand(String name, String... aliases) {
		return closure -> Bukkit.getServer().getCommandMap().register(name, new BukkitCommand(name, "", "", Arrays.asList(aliases)) {
			@Override
			public boolean execute(CommandSender sender, String s, String[] strings) {
				CraftPlayer player = sender instanceof CraftPlayer ? (CraftPlayer) sender : null;
				closure.setDelegate(new CommandContext(player, sender, strings));
				Object response = closure.call();
				if (response != null) sender.sendMessage(String.valueOf(response).replace('&', '§'));
				return true;
			}
		});
	}

}
