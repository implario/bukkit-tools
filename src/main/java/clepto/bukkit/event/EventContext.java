package clepto.bukkit.event;

import clepto.bukkit.B;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class EventContext implements EventContextProxy {

	public final Listener listener = new Listener() {};
	private final Predicate<Event> filter;

	public <T extends Event> void on(Class<T> type, EventPriority priority, Consumer<T> action) {
		Bukkit.getPluginManager().registerEvent(type, listener, priority, (listener1, event) -> {
			if (type.isInstance(event) && filter.test(event)) action.accept((T) event);
		}, B.plugin);
	}

	@Override
	public EventContext getEventContext() {
		return this;
	}

}
