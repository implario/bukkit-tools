package clepto.bukkit.routine;

import clepto.bukkit.groovy.GroovyUtils;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.function.Consumer;

public interface Routine {

	void setLimit(int times);

	void setPass(int pass);

	int getPass();

	void start();

	void cancel();

	long getTime();

	void setTime(long time);

	void addMainAction(Consumer<Routine> action);

	void addTerminateAction(Consumer<Routine> action);

	default Routine fluentStart(long millisMultiplier, Consumer<Routine> action) {
		this.setTime(this.getTime() * millisMultiplier);
		return fluentStart(action);
	}

	default Routine fluentStart(Consumer<Routine> action) {
		this.addMainAction(action);
		this.start();
		return this;
	}

	default Routine limit(int times) {
		this.setLimit(times);
		return this;
	}

	default Routine run(Consumer<Routine> action) {
		this.addMainAction(action);
		return this;
	}

	default Routine run(Runnable action) {
		return this.run(anything -> action.run());
	}

	default Routine run(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		return this.run(GroovyUtils.toConsumer(action));
	}

	default Routine whenFinished(Consumer<Routine> action) {
		this.addTerminateAction(action);
		return this;
	}

	default Routine whenFinished(Runnable action) {
		return this.whenFinished(anything -> action.run());
	}

	default Routine whenFinished(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		return this.whenFinished(GroovyUtils.toConsumer(action));
	}

	default Routine minutes(Runnable action) {
		return fluentStart(50 * 20 * 60, anything -> action.run());
	}

	default Routine seconds(Runnable action) {
		return fluentStart(50 * 20, anything -> action.run());
	}

	default Routine ticks(Runnable action) {
		return fluentStart(50, anything -> action.run());
	}

	default Routine minutes(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		return this.minutes(GroovyUtils.toConsumer(action));
	}

	default Routine minutes(Consumer<Routine> action) {
		return fluentStart(50 * 20 * 60, action);
	}

	default Routine seconds(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		return this.seconds(GroovyUtils.toConsumer(action));
	}

	default Routine seconds(Consumer<Routine> action) {
		return fluentStart(50 * 20, action);
	}

	default Routine ticks(@DelegatesTo (value = Routine.class, strategy = Closure.DELEGATE_FIRST) Closure<?> action) {
		return this.ticks(GroovyUtils.toConsumer(action));
	}

	default Routine ticks(Consumer<Routine> action) {
		return fluentStart(50, action);
	}

}
