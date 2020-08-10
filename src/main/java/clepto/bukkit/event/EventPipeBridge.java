package clepto.bukkit.event;

import java.util.function.Function;

@FunctionalInterface
public interface EventPipeBridge<T, U> extends Function<T, EventPipe<U>> {

	@Override
	EventPipe<U> apply(T t);

}
