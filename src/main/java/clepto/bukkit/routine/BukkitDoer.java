package clepto.bukkit.routine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class BukkitDoer implements Doer {

	private final Plugin plugin;
	private final List<Routine> routines = new ArrayList<>();

	public BukkitRoutine after(long time) {
		BukkitRoutine routine = new BukkitRoutine(plugin, false, time);
		routine.addTerminateAction(routines::remove);
		routines.add(routine);
		return routine;
	}

	public BukkitRoutine every(long time) {
		BukkitRoutine routine = new BukkitRoutine(plugin, true, time);
		routine.addTerminateAction(routines::remove);
		routines.add(routine);
		return routine;
	}

	@Override
	public Routine simple() {
		BukkitRoutine routine = new BukkitRoutine(plugin, false, 0);
		routine.addTerminateAction(routines::remove);
		routines.add(routine);
		return routine;
	}

	@Override
	public void cancelAll() {
		while (!routines.isEmpty()) {
			Routine routine = routines.get(0);
			routine.cancel();
			if (routines.isEmpty()) break;
			// First element should change after calling cancel()
			if (routines.get(0) == routine) {
				throw new IllegalStateException("Unable to cancel routine");
			}
		}
	}

}
