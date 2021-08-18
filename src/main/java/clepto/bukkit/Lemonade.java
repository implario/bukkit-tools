package clepto.bukkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Lemonade {

	@Getter
	private static final Map<String, Lemonade> dictionary = new HashMap<>();

	@ToString.Include
	private final List<String> before;

	@ToString.Include
	private final List<String> after;

	private final List<Consumer<ItemStack>> coreComposers;

	@ToString.Include
	private String key;
	private ItemStack cachedItem;
	private DynamicItem cachedDynamicItem;

	protected void render(Composer composer) {
		for (String parent : before) {
			Lemonade lemonade = get(parent);
			if (lemonade != null) lemonade.render(composer);
			else composer.composeCore(item -> {
				item.setType(Material.getMaterial(parent.toUpperCase().replace('-', '_')));
			});
		}
		coreComposers.forEach(composer::composeCore);

		for (String child : after) {
			Lemonade lemonade = get(child);
			if (lemonade != null) lemonade.render(composer);
			else composer.composeCore(item -> {
				item.setType(Material.getMaterial(child.toUpperCase().replace('-', '_')));
			});
		}
	}

	public DynamicItem dynamic() {
		return cachedDynamicItem == null ? cachedDynamicItem = new DynamicItem(render()) : cachedDynamicItem;
	}

	public ItemStack render() {
		if (cachedItem != null) return cachedItem;
		Composer composer = new Composer();
		render(composer);
		return cachedItem = composer.getItem();
	}

	public static ItemStack render(Lemonade... lemonades) {
		Composer composer = new Composer();
		for (Lemonade lemonade : lemonades) lemonade.render(composer);
		return composer.getItem();
	}

	public static Lemonade merge(Lemonade... lemonades) {
		List<String> parents = new ArrayList<>();
		for (Lemonade lemonade : lemonades) {
			if (lemonade.key == null) throw new InvalidConfigException("Tried to merge non-registered lemonade: " + lemonade);
			parents.add(lemonade.key);
		}
		return new Lemonade(parents, new ArrayList<>(), new ArrayList<>());
	}

	public static Lemonade get(String key) {
		return dictionary.get(key.toUpperCase());
	}

	public Lemonade register(String key) {
		dictionary.put(key.toUpperCase(), this);
		this.key = key;
		return this;
	}

	public static Lemonade parse(ConfigurationSection yml) {

		List<String> before = new ArrayList<>();
		List<String> after = new ArrayList<>();
		List<Consumer<ItemStack>> coreComposers = new ArrayList<>();

		// Тип предмета
		String icon = yml.getString("icon");
		if (icon != null) {
			for (String arg : icon.split(" ")) {
				if (arg.length() == 0) continue;
				if (arg.charAt(0) != '(' || arg.charAt(arg.length() - 1) != ')') before.add(arg);
				else after.add(arg.substring(1, arg.length() - 1));
			}
		}

		short data = (short) yml.getInt("data");
		if (data != 0) coreComposers.add(item -> item.setDurability(data));

		// Количество
		int amount = yml.getInt("amount");
		if (amount > 0) coreComposers.add(item -> item.setAmount(amount));


		// text - революция в мире майнкрафта, displayName и lore в одном!
		List<String> text;

		String textStr = yml.getString("text");

		if (textStr == null) text = null;
		else {
			String[] words = textStr.split("\n");
			text = Arrays.stream(words).map(line -> "§f" + line.replace('&', '§')).collect(Collectors.toList());
		}

		boolean override = yml.getBoolean("override");

		if (text != null) {
			if (override) coreComposers.add(m -> Lemonade.applyText(m, text));
			else coreComposers.add(m -> {
				String displayName = m.getDisplayName();
				List<String> oldLore = m.getLore();

				List<String> newText = new ArrayList<>();
				if (displayName != null) newText.add(displayName);
				if (oldLore != null) newText.addAll(oldLore);
				newText.addAll(text);
				Lemonade.applyText(m, newText);
			});
		}

		// Тип моба для яиц призыва
		String mobtype = yml.getString("mob");
		EntityType spawnedEntity = mobtype == null ? null : parseEntityType(mobtype, yml);

//		if (spawnedEntity != null) metaComposers.add(m -> ((SpawnEggMeta) m).setSpawnedType(spawnedEntity));


		// Цвет глины, стеклянных панелек, бетона и т. п.
		// ToDo: Fix without color dependency
//		String color = yml.getString("color");
//		if (color != null) {
//			try {
//				byte woolData = (byte) Color.valueOf(color.toUpperCase()).getWoolData();
//				coreComposers.add(item -> item.setDurability(woolData));
//			} catch (IllegalArgumentException ex) {
//				System.out.println("В душе ниибу шо за цвет " + color);
//			}
//		}

		List<String> enchantments = yml.getStringList("enchant");
		if (enchantments != null) {
//			if (override) coreComposers.add(m -> m.getEnchants().keySet().forEach(m::removeEnchant));
			coreComposers.add(m -> {
				for (String e : enchantments) parseAndApplyEnchantment(e, yml, m);
			});
		}

		int armorColor = yml.getInt("armor-color");
		if (armorColor > 0) {
			coreComposers.add(item -> {
				NBTTagCompound tag = item.handle.getOrCreateTag();
				NBTTagCompound display = tag.getCompound("display");
				if (display == null) {
					display = new NBTTagCompound();
					tag.set("display", display);
				}
				display.setInt("color", armorColor);
			});
		}

		if (yml.getBoolean("unbreakable")) coreComposers.add(m -> m.setUnbreakable(true));

		//todo
//		List<String> itemFlags = yml.getStringList("flags");
//		if (itemFlags != null) {
//			for (String flag : itemFlags) {
//				try {
//					if (flag.equals("*")) metaComposers.add(m -> m.addItemFlags(ItemFlag.values()));
//					else {
//						ItemFlag itemFlag = ItemFlag.valueOf(flag.toUpperCase());
//						metaComposers.add(m -> m.addItemFlags(itemFlag));
//					}
//				} catch (IllegalArgumentException ex) {
//					System.out.println("Флаг " + flag + " это какая-то фигня, убери его из '" + yml.getCurrentPath() + "'");
//				}
//			}
//		}

//		String skin = yml.getString("skin");
//		if (skin != null) {
//			metaComposers.add(m -> ((CraftMetaSkull) m).profile = B.createDummyProfile(skin));
//		}


		ConfigurationSection nbts = yml.getConfigurationSection("nbt");
		if (nbts != null) {
			coreComposers.add(item -> {
				net.minecraft.server.v1_12_R1.ItemStack nmsItem = item.handle;
				for (String nbtKey : nbts.getKeys(false)) {
					Object nbtValue = nbts.get(nbtKey);
					if (nmsItem.tag == null) nmsItem.tag = new NBTTagCompound();
					if (nbtValue instanceof Integer) nmsItem.tag.setInt(nbtKey, (Integer) nbtValue);
					else if (nbtValue instanceof Double) nmsItem.tag.setDouble(nbtKey, (Double) nbtValue);
					else if (nbtValue instanceof String) nmsItem.tag.setString(nbtKey, (String) nbtValue);
					else if (nbtValue instanceof List) {
						NBTTagList list = new NBTTagList();
						for (Object o : (List<?>) nbtValue) {
							String s = String.valueOf(o);
							list.add(new NBTTagString(s));
						}
						nmsItem.tag.set(nbtKey, list);
					}
					else System.out.println("Непонятное значение тега: '" + nbtValue + "', path: " + nbts.getCurrentPath());
				}
			});
		}

		return new Lemonade(before, after, coreComposers);
	}
	//	public static Lemonade merge(Lemonade... lemonades) {
	//		Lemonade lemonade = new Lemonade()

//	}

	private static void applyText(ItemStack meta, List<String> text) {
		Iterator<String> iterator = text.iterator();
		meta.setDisplayName(iterator.hasNext() ? iterator.next() : null);
		List<String> lore = new ArrayList<>();
		while (iterator.hasNext()) lore.add(iterator.next());
		meta.setLore(lore.isEmpty() ? null : lore);
	}

	private static void parseAndApplyEnchantment(String str, ConfigurationSection yml, ItemStack meta) {
		String[] split = str.split("-");
		if (split.length < 2) {
			System.out.println("Неверный формат зачарования: '" + str + "' в предмете '" + yml.getCurrentPath() + "'");
			return;
		}
		Enchantment ench = Enchantment.getById(Integer.parseInt(split[0]));
		if (ench == null) {
			System.out.println("Неизвестное зачарование: " + split[0] + " в предмете '" + yml.getCurrentPath() + "'");
			return;
		}
		try {
			int level = Integer.parseInt(split[1]);
//			if (meta instanceof EnchantmentStorageMeta) ((EnchantmentStorageMeta) meta).addStoredEnchant(ench, level, true);
//			else meta.addEnchant(ench, level, true);
			meta.addUnsafeEnchantment(ench, level);
		} catch (IllegalArgumentException ex) {
			System.out.println("Некорректный уровень зачарования: " + split[1] + " в предмете '" + yml.getCurrentPath() + "'");
		}
	}

	private static EntityType parseEntityType(String type, ConfigurationSection section) {
		try {
			return EntityType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException ex) {
			System.out.println("кто такой " + type + " в предмете '" + section.getCurrentPath() + "', я ево не знаю, он летучая мыш?");
			return EntityType.BAT;
		}
	}
}
