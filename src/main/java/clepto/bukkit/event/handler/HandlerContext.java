package clepto.bukkit.event.handler;

import clepto.bukkit.event.EventPipe;
import clepto.bukkit.event.EventPipeBridge;
import clepto.bukkit.event.PayloadBridge;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class HandlerContext<T, P> {

	public final int order;
	protected final PayloadBridge<P> payloadBridge;
	protected final EventPipeBridge<T, P> eventPipeBridge;

	public abstract EventPipe<P> getEventPipe(World world, UUID playerId);

}
