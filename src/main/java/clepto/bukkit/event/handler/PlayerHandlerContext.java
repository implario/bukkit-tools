package clepto.bukkit.event.handler;

import clepto.bukkit.event.EventPipe;
import clepto.bukkit.event.EventPipeBridge;
import clepto.bukkit.event.PlayerWrapper;
import clepto.bukkit.event.PayloadBridge;
import org.bukkit.World;

import java.util.UUID;

public class PlayerHandlerContext<P extends PlayerWrapper> extends HandlerContext<UUID, P> {

	public PlayerHandlerContext(int order,
								PayloadBridge<P> payloadBridge,
								EventPipeBridge<UUID, P> eventPipeBridge) {
		super(order, payloadBridge, eventPipeBridge);
	}

	@Override
	public EventPipe<P> getEventPipe(World world, UUID playerId) {
		return eventPipeBridge.apply(playerId);
	}

}
