package clepto.bukkit.event;

import org.bukkit.event.Event;

@FunctionalInterface
public interface BooleanEventProcessor<E extends Event> {

	boolean process(E event, EventPipe pipe, Object payload);

}

