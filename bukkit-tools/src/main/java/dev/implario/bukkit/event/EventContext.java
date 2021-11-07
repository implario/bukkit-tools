package dev.implario.bukkit.event;

import dev.implario.bukkit.platform.Platforms;
import dev.implario.bukkit.routine.Routine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Data
@AllArgsConstructor
public class EventContext implements Listener {

    private Predicate<Event> filter;

    private final EventContext owner;

    private final List<EventContext> children = new ArrayList<>();

    public EventContext(Predicate<Event> filter) {
        this.filter = filter;
        this.owner = null;
    }

    public EventContext fork() {
        EventContext context = new EventContext(this.filter, this);
        children.add(context);
        return context;
    }

    public void unregisterAll() {
        HandlerList.unregisterAll(this);
        if (owner != null) owner.children.remove(this);

        for (EventContext child : new ArrayList<>(children)) {
            child.unregisterAll();
        }
    }

    public void appendOption(Predicate<Event> filter) {
        Predicate<Event> previous = this.filter;
        this.filter = e -> previous.test(e) || filter.test(e);
    }

    public void appendRequirement(Predicate<Event> filter) {
        Predicate<Event> previous = this.filter;
        this.filter = e -> previous.test(e) && filter.test(e);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> EventContext on(Class<T> type, EventPriority priority, Consumer<T> action) {
        Bukkit.getPluginManager().registerEvent(type, this, priority, (listener1, event) -> {
            if (type.isInstance(event) && filter.test(event)) action.accept((T) event);
        }, Platforms.getPlugin());
        return this;
    }

    public <T extends Event> EventContext on(Class<T> type, Consumer<T> handler) {
        return on(type, EventPriority.NORMAL, handler);
    }

    public <T extends Event> EventContext before(Class<T> type, Consumer<T> handler) {
        return on(type, EventPriority.LOW, handler);
    }

    public <T extends Event> EventContext after(Class<T> type, Consumer<T> handler) {
        return on(type, EventPriority.HIGH, handler);
    }

    public <T extends Event & Cancellable> EventContext cancel(Class<T> type) {
        return cancel(type, EventPriority.NORMAL);
    }

    public <T extends Event & Cancellable> EventContext cancel(Class<T> type, EventPriority priority) {
        return on(type, priority, e -> e.setCancelled(true));
    }

    public <T extends Event & Cancellable> EventContext filterOn(Class<T> type, EventPriority priority, Predicate<T> action) {
        return on(type, priority, event -> {
            boolean accepted = action.test(event);
            if (!accepted) event.setCancelled(true);
        });
    }

    public <T extends Event & Cancellable> EventContext filter(Class<T> type, Predicate<T> handler) {
        return filterOn(type, EventPriority.NORMAL, handler);
    }

    public <T extends Event & Cancellable> EventContext filterBefore(Class<T> type, Predicate<T> handler) {
        return filterOn(type, EventPriority.LOW, handler);
    }

    public <T extends Event & Cancellable> EventContext filterAfter(Class<T> type, Predicate<T> handler) {
        return filterOn(type, EventPriority.HIGH, handler);
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
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Platforms.getPlugin(), () -> {
            routine.setNextPassTime(System.currentTimeMillis() + ticks * 50);
            routine.getAction().accept(routine);
            routine.setPass(routine.getPass() + 1);
            if (routine.getPassLimit() != 0 && routine.getPass() >= routine.getPassLimit()) {
                routine.getKillHandler().accept(routine);
            }
        }, ticks, ticks);
        routine.setKillHandler(r -> Bukkit.getScheduler().cancelTask(taskId));

        return routine;

    }

}
