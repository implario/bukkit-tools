package clepto.bukkit.routine;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.function.Consumer;

public class Routine implements Runnable {

	private static Routine activeTask;

	private final Plugin plugin;
	private final boolean repeat;
	private final List<Routine> routines;
	private long ticks;
	private long limit = -1;
	public long pass;
	private Runnable action;
	private Runnable whenFinished;
	public BukkitTask bukkitTask;

	public Routine(Plugin plugin, boolean repeat, long ticks, List<Routine> routines) {
		this.plugin = plugin;
		this.repeat = repeat;
		this.ticks = ticks;
		this.routines = routines;
	}

	public Routine limit(long limit) {
		this.limit = limit;
		return this;
	}

	public Routine whenFinished(Consumer<Routine> action) {
		this.whenFinished = () -> action.accept(this);
		return this;
	}

	public Routine whenFinished(Runnable action) {
		this.whenFinished = action;
		return this;
	}

	public Routine whenFinished(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		action.setDelegate(this);
		this.whenFinished = action;
		return this;
	}

	public Routine ticks(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		this.action = action;
		action.setDelegate(this);
		this.start();
		return this;
	}

	public Routine ticks(Consumer<Routine> action) {
		this.action = () -> action.accept(this);
		this.start();
		return this;
	}

	public Routine ticks(Runnable action) {
		this.action = action;
		this.start();
		return this;
	}

	public Routine seconds(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		this.ticks *= 20;
		action.setDelegate(this);
		this.action = action;
		this.start();
		return this;
	}

	public Routine seconds(Consumer<Routine> action) {
		this.ticks *= 20;
		this.action = () -> action.accept(this);
		this.start();
		return this;
	}

	public Routine seconds(Runnable action) {
		this.ticks *= 20;
		this.action = action;
		this.start();
		return this;
	}

	public Routine minutes(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		this.ticks *= 1200;
		action.setDelegate(this);
		this.action = action;
		this.start();
		return this;
	}

	public Routine minutes(Consumer<Routine> action) {
		this.ticks *= 1200;
		this.action = () -> action.accept(this);
		this.start();
		return this;
	}

	public Routine minutes(Runnable action) {
		this.ticks *= 1200;
		this.action = action;
		this.start();
		return this;
	}


	public void start() {

		BukkitScheduler scheduler = Bukkit.getScheduler();
		bukkitTask = repeat ?
				scheduler.runTaskTimer(plugin, this, ticks, ticks) :
				scheduler.runTaskLater(plugin, this, ticks);

	}

	public void cancel() {
		if (this.bukkitTask != null) this.bukkitTask.cancel();
		this.finish();
	}

	private void finish() {
		if (this.whenFinished != null) {
			this.whenFinished.run();
			this.whenFinished = null;
		}
		this.routines.remove(this);
	}

	@Override
	public void run() {
		Routine parent = activeTask;
		activeTask = this;
		this.action.run();
		this.pass++;
		if (!this.repeat)
			this.finish();
		else if (this.limit >= 0 && this.pass >= this.limit)
			this.cancel();
		activeTask = parent;
	}

	public static void exit() {
		if (activeTask == null) new RuntimeException("Tried to exit when outside a task").printStackTrace();
		else activeTask.cancel();
	}

}
