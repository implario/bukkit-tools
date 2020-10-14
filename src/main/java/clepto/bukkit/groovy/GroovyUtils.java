package clepto.bukkit.groovy;

import groovy.lang.Closure;

import java.util.function.Consumer;

public class GroovyUtils {

	public static <T> Consumer<T> toConsumer(Closure<?> closure) {
		return object -> {
			closure.setResolveStrategy(Closure.DELEGATE_FIRST);
			closure.setDelegate(object);
			closure.run();
		};
	}

}
