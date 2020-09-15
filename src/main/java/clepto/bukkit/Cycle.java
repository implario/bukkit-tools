package clepto.bukkit;

import org.bukkit.Bukkit;

public class Cycle implements Runnable {

	private static int activeTask = -1;

	private final Task task;
	private final int iterations;
	private final int id;
	private int ticks;

	public Cycle(int interval, int iterations, Task task) {
		this.task = task;
		this.iterations = iterations;
		this.id = B.repeat(interval, this);
	}

	@Override
	public void run() {
		int cache = activeTask;
		activeTask = id;
		task.execute(ticks++);
		if (iterations >= 0 && ticks > iterations) Bukkit.getScheduler().cancelTask(id);
		activeTask = cache;
	}

	public static void run(int interval, int iterations, Task task) {
		new Cycle(interval, iterations, task);
	}

	public static void exit() {
		if (activeTask < 0) new RuntimeException("Tried to exit when outside a task").printStackTrace();
		else Bukkit.getScheduler().cancelTask(activeTask);
	}

	public interface Task {
		void execute(int tickId);
	}

}
