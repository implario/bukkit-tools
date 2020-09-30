package clepto.bukkit.command;

import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

@Data
public class CommandContext {

	private final CraftPlayer player;
	private final CommandSender sender;
	private final String[] args;

	public CraftPlayer getPlayer() {
		if (this.player == null) this.sender.sendMessage("This commands requires to be executed by a player.");
		return this.player;
	}

	public void message(Object... messages) {
		for (Object message : messages) {
			sender.sendMessage(String.valueOf(message).replace('&', '§'));
		}
	}

}
