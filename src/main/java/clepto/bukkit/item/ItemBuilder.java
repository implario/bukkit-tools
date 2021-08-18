package clepto.bukkit.item;

import clepto.bukkit.groovy.GroovyUtils;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings ({"rawtypes", "deprecation", "unchecked"})
@NoArgsConstructor
@Setter
@Getter
@Accessors (chain = true, fluent = true)
public class ItemBuilder {

	public Material item;
	public int amount = 1;
	public int data;
	public final Map<String, Object> nbt = new HashMap<>();
	public final List<String> text = new ArrayList<>();
	public Object context;

	public ItemBuilder(Object context) {
		this.context = context;
	}

	public ItemBuilder nbt(String key, Object value) {
		this.nbt0(this.nbt, Collections.singletonMap(key, value));
		return this;
	}

	public ItemBuilder nbt(Map<String, Object> map) {
		this.nbt0(this.nbt, map);
		return this;
	}

	private void nbt0(Map receiver, Map source) {
		for (Object entry : source.entrySet()) {
			String key = String.valueOf(((Map.Entry<?, ?>) entry).getKey());
			Object value = ((Map.Entry<?, ?>) entry).getValue();
			if (value instanceof Map) {
				Object existing = receiver.get(key);
				Map<?, ?> branch = existing instanceof Map ? (Map<?, ?>) existing : new HashMap<>();
				this.nbt0(branch, (Map<?, ?>) value);
				receiver.put(key, branch);
			} else {
				receiver.put(key, value);
			}
		}
	}

	public ItemBuilder armorColor(int color) {
		return this.nbt("display", Collections.singletonMap("color", color));
	}

	public ItemBuilder enchant(Enchantment enchantment, int level) {
		val enchantData = new HashMap<>();
		enchantData.put("id", (short) enchantment.getId());
		enchantData.put("lvl", (short) level);
		val enchantList = new ArrayList<>();
		enchantList.add(enchantData);
		return this.nbt("ench", enchantList);
	}


	public ItemBuilder apply(@DelegatesTo(value = ItemBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure) {
		return this.apply(closure, null);
	}

	public ItemBuilder apply(@DelegatesTo(value = ItemBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure<?> closure, Object context) {
		return this.apply(GroovyUtils.toConsumer(closure), context);
	}

	public ItemBuilder apply(Consumer<ItemBuilder> function) {
		return this.apply(function, null);
	}

	public ItemBuilder apply(Consumer<ItemBuilder> function, Object context) {
		Object oldContext = this.context;
		if (context != null) this.context = context;
		function.accept(this);
		this.context = oldContext;
		return this;
	}

	public ItemBuilder text(String text) {
		List<String> list = Arrays.asList(text.split("\n"));
		if (list.isEmpty()) return this;
		int emptyLines = 0;
		boolean content = false;
		for (String line : list) {
			line = line.trim();
			if (!line.isEmpty()) {
				for (int i = 0; i < emptyLines; i++) this.text.add("§f");
				this.text.add("§f" + line.replace('&', '§'));
				content = true;
				emptyLines = 0;
			} else if (content) emptyLines++;
		}
		return this;
	}

	public ItemStack build() {
		ItemStack item = new ItemStack(CraftMagicNumbers.getItem(this.item), amount, data);

		if (!this.text.isEmpty()) {
			Map<String, Object> displayMap = new HashMap<>();
			Iterator<String> iterator = this.text.iterator();
			displayMap.put("Name", iterator.next());
			if (iterator.hasNext()) {
				List<String> lore = new ArrayList<>();
				while (iterator.hasNext())
					lore.add(iterator.next());
				displayMap.put("Lore", lore);
			}
			this.nbt("display", displayMap);
		}

		if (!this.nbt.isEmpty()) {
			item.empty = false;
			item.tag = (NBTTagCompound) toNbt(this.nbt);
		}

		return item;
	}

	public static NBTBase toNbt(Object object) {
		if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			NBTTagCompound nbtMap = new NBTTagCompound();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				NBTBase value = toNbt(entry.getValue());
				if (value != null)
					nbtMap.set(String.valueOf(entry.getKey()), value);
			}
			return nbtMap;
		}
		if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			NBTTagList nbtList = new NBTTagList();
			for (Object o : collection) {
				NBTBase value = toNbt(o);
				if (value != null) nbtList.add(value);
			}
			return nbtList;
		}
		if (object.getClass() == Integer.class) return new NBTTagInt((Integer) object);
		if (object.getClass() == Double.class) return new NBTTagDouble((Double) object);
		if (object.getClass() == Float.class) return new NBTTagFloat((Float) object);
		if (object.getClass() == Long.class) return new NBTTagLong((Long) object);
		if (object.getClass() == Short.class) return new NBTTagShort((Short) object);
		if (object.getClass() == Byte.class) return new NBTTagByte((Byte) object);
		if (object.getClass() == byte[].class) return new NBTTagByteArray((byte[]) object);
		if (object instanceof CharSequence) return new NBTTagString(String.valueOf(object));
		return null;
	}

	public static Object fromNbt(NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			Map<String, Object> map = new HashMap<>();
			NBTTagCompound nbtMap = (NBTTagCompound) nbt;
			for (Map.Entry<String, NBTBase> entry : nbtMap.map.entrySet()) {
				map.put(entry.getKey(), fromNbt(entry.getValue()));
			}
			return map;
		}
		if (nbt instanceof NBTTagList) {
			Collection<Object> collection = new ArrayList<>();
			NBTTagList nbtList = (NBTTagList) nbt;
			for (NBTBase o : nbtList.list) collection.add(fromNbt(o));
			return collection;
		}
		if (nbt.getClass() == NBTTagInt.class) return ((NBTTagInt) nbt).data;
		if (nbt.getClass() == NBTTagDouble.class) return ((NBTTagDouble) nbt).data;
		if (nbt.getClass() == NBTTagFloat.class) return ((NBTTagFloat) nbt).data;
		if (nbt.getClass() == NBTTagLong.class) return ((NBTTagLong) nbt).data;
		if (nbt.getClass() == NBTTagShort.class) return ((NBTTagShort) nbt).data;
		if (nbt.getClass() == NBTTagByte.class) return ((NBTTagByte) nbt).data;
		if (nbt.getClass() == NBTTagByteArray.class) return ((NBTTagByteArray) nbt).data;
		if (nbt.getClass() == NBTTagString.class) return ((NBTTagString) nbt).data;
		return null;
	}

}
