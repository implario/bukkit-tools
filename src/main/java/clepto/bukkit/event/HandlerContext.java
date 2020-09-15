package clepto.bukkit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HandlerContext<P> {

	public final int order;
	protected final PayloadBridge<P> payloadBridge;
	protected final EventPipeBridge<P> eventPipeBridge;

}
