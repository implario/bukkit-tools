package clepto.bukkit;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;

import java.util.Collection;
import java.util.Iterator;

public class ComplexRegistry<ID, T> implements Iterable<T> {

	private final Reference2ReferenceMap<ID, T> forward = new Reference2ReferenceLinkedOpenHashMap<>();
	private final Reference2ReferenceMap<T, ID> reverse = new Reference2ReferenceLinkedOpenHashMap<>();

	public ComplexRegistry<ID, T> register(ID id, T object) {
		forward.put(id, object);
		reverse.put(object, id);
		return this;
	}

	public ID getId(T object) {
		return reverse.get(object);
	}

	public T get(ID id) {
		return forward.get(id);
	}

	public Collection<T> getAll() {
		return forward.values();
	}

	@Override
	public Iterator<T> iterator() {
		return forward.values().iterator();
	}

	public int size() {
		return forward.size();
	}

	public ObjectSet<Reference2ReferenceMap.Entry<ID, T>> entrySet() {
		return forward.reference2ReferenceEntrySet();
	}

//	public void encode(Encoder encoder, ToIntFunction<ID> keySerializer, Serializer<T> valueSerializer) {
//		encoder.writeInt(size());
//		for (Map.Entry<ID, T> e : forward.entrySet()) {
//			encoder.writeInt(keySerializer.applyAsInt(e.getKey()));
//			encoder.write(e.getValue(), valueSerializer);
//		}
//	}
//
//	public static <ID, T> ComplexRegistry<ID, T> decode(Decoder decoder, IntFunction<ID> keyDeserializer, Deserializer<T> valueDeserializer) {
//		int size = decoder.readInt();
//		ComplexRegistry<ID, T> registry = new ComplexRegistry<>();
//		for (int i = 0; i < size; i++) {
//			registry.register(keyDeserializer.apply(decoder.readInt()), decoder.read(valueDeserializer));
//		}
//		return registry;
//	}

}
