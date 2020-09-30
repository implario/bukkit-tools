package clepto.bukkit.command;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public interface Command {

	void handle(@DelegatesTo(CommandContext.class) Closure<?> closure);

}
