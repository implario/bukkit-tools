package clepto.bukkit.routine;

import java.util.List;

public interface Doer {

	Routine after(long time);

	Routine every(long time);

	Routine simple();

	List<? extends Routine> getRoutines();

//	ToDo: void cancelCurrent();

	void cancelAll();

	interface Proxy extends Doer {

		Doer getDoer();

		@Override
		default List<? extends Routine> getRoutines() {
			return this.getDoer().getRoutines();
		}

		@Override
		default Routine after(long time) {
			return getDoer().after(time);
		}

		@Override
		default Routine every(long time) {
			return getDoer().every(time);
		}

		@Override
		default Routine simple() {
			return getDoer().simple();
		}

		@Override
		default void cancelAll() {
			this.getDoer().cancelAll();
		}

//		@Override
//		default void cancelCurrent() {
//			this.getDoer().cancelCurrent();
//		}

	}

}
