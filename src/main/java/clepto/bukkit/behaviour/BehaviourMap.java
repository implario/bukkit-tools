package clepto.bukkit.behaviour;

import groovy.lang.Closure;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

@RequiredArgsConstructor
public class BehaviourMap<T extends Enum<T>> implements Map<T, Closure<?>> {

	private final String name;
	private final Map<T, Closure<?>> delegate;

	public BehaviourMap(Class<T> type, String name) {
		this.name = name;
		this.delegate = new EnumMap<>(type);
	}

	public void call(Material material, Object context) {
		try {
			Closure<?> closure = this.get(material);
			if (closure == null) return;
			closure.setDelegate(context);
			closure.call();
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING, "An exception occurred while processing " + material + " " + this.name + " behaviour:", ex);
		}
	}

	@Override
	public int size() {
		return this.delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return this.delegate.containsKey(o);
	}

	@Override
	public boolean containsValue(Object o) {
		return this.delegate.containsValue(o);
	}

	@Override
	public Closure<?> get(Object o) {
		return this.delegate.get(o);
	}

	@Override
	public Closure<?> put(T key, Closure<?> closure) {
		return this.delegate.put(key, closure);
	}

	@Override
	public Closure<?> remove(Object o) {
		return this.delegate.remove(o);
	}

	@Override
	public void putAll(Map<? extends T, ? extends Closure<?>> map) {
		this.delegate.putAll(map);
	}

	@Override
	public void clear() {
		this.delegate.clear();
	}

	@Override
	public Set<T> keySet() {
		return this.delegate.keySet();
	}

	@Override
	public Collection<Closure<?>> values() {
		return this.delegate.values();
	}

	@Override
	public Set<Entry<T, Closure<?>>> entrySet() {
		return this.delegate.entrySet();
	}

}
