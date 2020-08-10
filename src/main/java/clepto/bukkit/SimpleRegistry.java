package clepto.bukkit;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;

import java.util.Collection;
import java.util.Iterator;

public class SimpleRegistry<T> implements Iterable<T> {

	private final Int2ReferenceMap<T> forward = new Int2ReferenceArrayMap<>();
	private final Reference2IntMap<T> reverse = new Reference2IntArrayMap<>();

	public SimpleRegistry<T> register(int id, T object) {
		forward.put(id, object);
		reverse.put(object, id);
		return this;
	}

	public int getId(T object) {
		return reverse.getInt(object);
	}

	public T get(int id) {
		return forward.get(id);
	}

	public Collection<T> getAll() {
		return forward.values();
	}

	public T remove(int id) {
		T object = forward.remove(id);
		reverse.remove(object);
		return object;
	}

	@Override
	public Iterator<T> iterator() {
		return forward.values().iterator();
	}

	public int size() {
		return forward.size();
	}

	public ObjectSet<Int2ReferenceMap.Entry<T>> entrySet() {
		return forward.int2ReferenceEntrySet();
	}

//
//	public void encode(Encoder encoder, Serializer<T> valueSerializer) {
//		encoder.writeInt(size());
//		for (Int2ReferenceMap.Entry<T> e : forward.int2ReferenceEntrySet()) {
//			encoder.writeInt(e.getIntKey());
//			encoder.write(e.getValue(), valueSerializer);
//		}
//	}
//
//	public static <T> SimpleRegistry<T> decode(Decoder decoder, Deserializer<T> valueDeserializer) {
//		int size = decoder.readInt();
//		SimpleRegistry<T> registry = new SimpleRegistry<>();
//		for (int i = 0; i < size; i++) {
//			registry.register(decoder.readInt(), decoder.read(valueDeserializer));
//		}
//		return registry;
//	}


}
