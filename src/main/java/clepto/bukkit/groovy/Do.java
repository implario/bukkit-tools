package clepto.bukkit.groovy;

import clepto.bukkit.B;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Do implements Runnable {

	private static Do activeTask;

	public static Do after(long time) {
		return new Do(false, time);
	}

	public static Do every(long time) {
		return new Do(true, time);
	}

	private final boolean repeat;
	private long ticks;
	private long limit = -1;
	public long pass;
	private Runnable action;
	private Runnable whenFinished;
	public BukkitTask bukkitTask;

	public Do(boolean repeat, long ticks) {
		this.repeat = repeat;
		this.ticks = ticks;
	}

	public Do limit(long limit) {
		this.limit = limit;
		return this;
	}

	public Do whenFinished(@DelegatesTo (value = Do.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		action.setDelegate(this);
		this.whenFinished = action;
		return this;
	}

	public Do ticks(@DelegatesTo (value = Do.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		this.action = action;
		action.setDelegate(this);
		this.start();
		return this;
	}

	public Do seconds(@DelegatesTo (value = Do.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		this.ticks *= 20;
		action.setDelegate(this);
		this.action = action;
		this.start();
		return this;
	}

	public Do minutes(@DelegatesTo (value = Do.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		this.ticks *= 1200;
		action.setDelegate(this);
		this.action = action;
		this.start();
		return this;
	}

	public void start() {

		BukkitScheduler scheduler = Bukkit.getScheduler();
		bukkitTask = repeat ?
				scheduler.runTaskTimer(B.plugin, this, ticks, ticks) :
				scheduler.runTaskLater(B.plugin, this, ticks);

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
	}

	@Override
	public void run() {
		Do parent = activeTask;
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
