package clepto.bukkit.routine;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public class BukkitRoutine implements Runnable, Routine {

	private final Plugin plugin;
	private final boolean repeat;

	private long time;
	private int limit = -1;
	private int pass;
	private List<Consumer<Routine>> mainActions;
	private List<Consumer<Routine>> terminateActions;
	public BukkitTask bukkitTask;

	public BukkitRoutine(Plugin plugin, boolean repeat, long millis) {
		this.plugin = plugin;
		this.repeat = repeat;
		this.time = millis;
	}

	@Override
	public void addMainAction(Consumer<Routine> action) {
		if (mainActions == null) mainActions = new ArrayList<>();
		mainActions.add(action);
	}

	@Override
	public void addTerminateAction(Consumer<Routine> action) {
		if (terminateActions == null) terminateActions = new ArrayList<>();
		terminateActions.add(action);
	}

	public void start() {

		BukkitScheduler scheduler = Bukkit.getScheduler();
		bukkitTask = repeat ?
				scheduler.runTaskTimer(plugin, this, time / 50, time / 50) :
				scheduler.runTaskLater(plugin, this, time / 50);

	}

	public void cancel() {
		if (this.bukkitTask != null) this.bukkitTask.cancel();
		this.finish();
	}

	private void finish() {
		if (this.terminateActions == null || this.terminateActions.isEmpty())
			return;

		for (Consumer<Routine> action : this.terminateActions)
			action.accept(this);

		this.terminateActions.clear();
	}

	@Override
	public void run() {
		if (this.mainActions != null && !this.mainActions.isEmpty()) {
			for (Consumer<Routine> action : this.mainActions) {
				action.accept(this);
			}
		}
		this.pass++;
		if (!this.repeat)
			this.finish();
		else if (this.limit >= 0 && this.pass >= this.limit)
			this.cancel();
	}


}
