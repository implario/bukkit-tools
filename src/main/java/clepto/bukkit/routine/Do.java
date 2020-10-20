package clepto.bukkit.routine;

import clepto.bukkit.B;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Do {

	private static Doer globalDoer;

	public Doer getGlobalDoer() {
		return globalDoer == null ? globalDoer = new BukkitDoer(B.plugin) : globalDoer;
	}


	public static List<? extends Routine> getRoutines() {
		return getGlobalDoer().getRoutines();
	}

	public static Routine after(long time) {
		return getGlobalDoer().after(time);
	}

	public static Routine every(long time) {
		return getGlobalDoer().every(time);
	}

	public static Routine simple() {
		return getGlobalDoer().simple();
	}

	public static void cancelAll() {
		getGlobalDoer().cancelAll();
	}

}
