package dev.implario.bukkit.item;

import dev.implario.bukkit.platform.Platforms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings({"rawtypes", "deprecation", "unchecked"})
@NoArgsConstructor
@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class ItemBuilder {

	public Material type = Material.STONE;
	public int amount = 1;
	public int data;
	public final Map<String, Object> nbt = new HashMap<>();
	public final List<String> text = new ArrayList<>();

	public ItemBuilder(Material type) {
		this.type = type;
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

	// todo: just do it
    public void skullOwner(String url) {
		type = Material.SKULL_ITEM;
		data = 3;

		Map<String, Object> nbtDisplay = new HashMap<>();
		Map<String, Object> nbtSkullOwner = new HashMap<>();
		Map<String, Object> nbtProperties = new HashMap<>();
		List<Object> nbtTextures = new ArrayList<>();
		Map<String, Object> nbtSkin = new HashMap<>();
		nbtSkin.put("Value", url);
		nbtTextures.add(nbtSkin);
		nbtProperties.put("textures", nbtTextures);
		nbtSkullOwner.put("Properties", nbtProperties);
		nbtSkullOwner.put("Id", UUID.randomUUID());
		nbtDisplay.put("SkullOwner", nbtSkullOwner);
		nbt("display", nbtDisplay);

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

		return Platforms.get().createItemStack(this.type, this.amount, this.data, this.nbt);

	}


}

