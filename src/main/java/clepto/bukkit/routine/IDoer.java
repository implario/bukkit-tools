package clepto.bukkit.routine;

import java.util.Collection;

public interface IDoer {

	Routine after(long time);

	Routine every(long time);

	Collection<? extends Routine> getRoutines();

	default void cancelAll() {
		for (Routine routine : getRoutines()) {
			routine.cancel();
		}
	}

	interface Proxy extends IDoer {

		IDoer getDoer();

		@Override
		default Routine after(long time) {
			return getDoer().after(time);
		}

		@Override
		default Routine every(long time) {
			return getDoer().every(time);
		}

	}

}
