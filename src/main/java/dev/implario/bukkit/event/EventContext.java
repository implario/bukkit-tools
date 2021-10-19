package dev.implario.bukkit.event;

import dev.implario.bukkit.platform.Platforms;
import dev.implario.bukkit.routine.Routine;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class EventContext {

    public final Listener listener = new Listener() {};

    private final Predicate<Event> filter;

    private final EventContext owner;

    private final List<EventContext> children = new ArrayList<>();

    public EventContext(Predicate<Event> filter) {
        this.filter = filter;
        this.owner = null;
    }

    public EventContext fork(Predicate<Event> filter) {
        EventContext context = new EventContext(this.filter.and(filter), this);
        children.add(context);
        return context;
    }

    public EventContext fork() {
        return fork(anything -> true);
    }

    public void unregisterAll() {
        HandlerList.unregisterAll(listener);
        if (owner != null) owner.children.remove(this);

        for (EventContext child : new ArrayList<>(children)) {
            child.unregisterAll();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void on(Class<T> type, EventPriority priority, Consumer<T> action) {
        Bukkit.getPluginManager().registerEvent(type, listener, priority, (listener1, event) -> {
            if (type.isInstance(event) && filter.test(event)) action.accept((T) event);
        }, Platforms.getPlugin());
    }

    public <T extends Event> void on(Class<T> type, Consumer<T> handler) {
        on(type, EventPriority.NORMAL, handler);
    }

    public <T extends Event> void before(Class<T> type, Consumer<T> handler) {
        on(type, EventPriority.LOW, handler);
    }

    public <T extends Event> void after(Class<T> type, Consumer<T> handler) {
        on(type, EventPriority.HIGH, handler);
    }

    public Routine every(long ticks, Consumer<Routine> action) {

        Routine routine = new Routine();
        routine.setInterval(ticks);
        routine.setAction(action);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Platforms.getPlugin(), () -> routine.getAction().accept(routine),
                ticks, ticks);

        return routine;

    }

    public Routine after(long ticks, Consumer<Routine> action) {

        Routine routine = new Routine();
        routine.setInterval(ticks);
        routine.setAction(action);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Platforms.getPlugin(), () -> routine.getAction().accept(routine),
                ticks);

        return routine;

    }

}
