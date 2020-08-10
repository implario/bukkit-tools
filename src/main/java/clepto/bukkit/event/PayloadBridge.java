package clepto.bukkit.event;

import java.util.UUID;
import java.util.function.Function;

@FunctionalInterface
public interface PayloadBridge<U> extends Function<UUID, U> {

	@Override
	U apply(UUID uuid);

}
