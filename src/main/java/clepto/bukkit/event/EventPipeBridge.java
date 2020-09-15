package clepto.bukkit.event;

import org.bukkit.World;

import java.util.UUID;

@FunctionalInterface
public interface EventPipeBridge<U> {

	EventPipe<U> apply(World world, UUID initiatorId);

}
