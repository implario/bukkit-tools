package clepto.bukkit.command;

import lombok.Getter;

public class Commands {

	@Getter
	private static final CommandManager manager = new DirtyCommandManager();

	public static Command registerCommand(String name, String... aliases) {
		return manager.registerCommand(name, aliases);
	}

}
