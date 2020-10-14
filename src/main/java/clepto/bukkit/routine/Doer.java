package clepto.bukkit.routine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Doer implements IDoer {

	private final Plugin plugin;
	private final List<Routine> routines = new ArrayList<>();

	public Routine after(long time) {
		Routine routine = new Routine(plugin, false, time, routines);
		routines.add(routine);
		return routine;
	}

	public Routine every(long time) {
		Routine routine = new Routine(plugin, true, time, routines);
		routines.add(routine);
		return routine;
	}

}
