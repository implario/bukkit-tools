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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Data
@AllArgsConstructor
public class EventContext implements Listener {

    private Predicate<Event> filter;

    private final EventContext owner;

    private final List<EventContext> children = new ArrayList<>();
    private final Set<Integer> routines = new HashSet<>();

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
        routines.forEach(Bukkit.getScheduler()::cancelTask);
        routines.clear();
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
    public <T extends Event> void on(Class<T> type, EventPriority priority, Consumer<T> action) {
        Bukkit.getPluginManager().registerEvent(type, this, priority, (listener1, event) -> {
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

    public <T extends Event & Cancellable> void cancel(Class<T> type) {
        cancel(type, EventPriority.NORMAL);
    }

    public <T extends Event & Cancellable> void cancel(Class<T> type, EventPriority priority) {
        on(type, priority, e -> e.setCancelled(true));
    }

    public <T extends Event & Cancellable> void filterOn(Class<T> type, EventPriority priority, Predicate<T> action) {
        on(type, priority, event -> {
            boolean accepted = action.test(event);
            if (!accepted) event.setCancelled(true);
        });
    }

    public <T extends Event & Cancellable> void filter(Class<T> type, Predicate<T> handler) {
        filterOn(type, EventPriority.NORMAL, handler);
    }

    public <T extends Event & Cancellable> void filterBefore(Class<T> type, Predicate<T> handler) {
        filterOn(type, EventPriority.LOW, handler);
    }

    public <T extends Event & Cancellable> void filterAfter(Class<T> type, Predicate<T> handler) {
        filterOn(type, EventPriority.HIGH, handler);
    }

    public Routine after(long ticks, Consumer<Routine> action) {

        Routine routine = new Routine();
        routine.setInterval(ticks);
        routine.setAction(action);
        int[] taskId = {0};
        taskId[0] = Bukkit.getScheduler().scheduleSyncDelayedTask(Platforms.getPlugin(), () -> {
            routines.remove(taskId[0]);
            routine.getAction().accept(routine);
        }, ticks);

        routines.add(taskId[0]);
        routine.setId(taskId[0]);

        return routine;
    }

    public Routine every(long ticks, Consumer<Routine> action) {

        Routine routine = new Routine();
        routine.setInterval(ticks);
        routine.setAction(action);
        int[] taskId = {0};
        taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(Platforms.getPlugin(), () -> {
            routine.setNextPassTime(System.currentTimeMillis() + ticks * 50);
            routine.getAction().accept(routine);
            routine.setPass(routine.getPass() + 1);
            if (routine.getPassLimit() != 0 && routine.getPass() >= routine.getPassLimit()) {
                routine.getKillHandler().accept(routine);
            }
        }, ticks, ticks);
        routines.add(taskId[0]);
        routine.setKillHandler(r -> {
            routines.remove(taskId[0]);
            Bukkit.getScheduler().cancelTask(taskId[0]);
        });

        return routine;

    }

}
