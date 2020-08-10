package clepto.bukkit.event.handler;

import clepto.bukkit.event.EventPipe;
import clepto.bukkit.event.EventPipeBridge;
import clepto.bukkit.event.PayloadBridge;
import org.bukkit.World;

import java.util.UUID;

public class WorldHandlerContext<P> extends HandlerContext<World, P> {

	public WorldHandlerContext(int order,
							   PayloadBridge<P> playerConverter,
							   EventPipeBridge<World, P> eventPipeBridge) {
		super(order, playerConverter, eventPipeBridge);
	}

	@Override
	public EventPipe<P> getEventPipe(World world, UUID playerId) {
		return eventPipeBridge.apply(world);
	}

}
