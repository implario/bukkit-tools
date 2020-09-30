package clepto.bukkit.command;

public interface CommandManager {

	Command registerCommand(String name, String... aliases);

	interface Proxy extends CommandManager {

		CommandManager getCommandManager();

		@Override
		default Command registerCommand(String name, String... aliases) {
			return this.getCommandManager().registerCommand(name, aliases);
		}

	}

}
