package clepto.bukkit.event;

import org.bukkit.event.Event;

@FunctionalInterface
public interface VoidEventProcessor<E extends Event> {

	void process(E event, EventPipe pipe, Object payload);

}
