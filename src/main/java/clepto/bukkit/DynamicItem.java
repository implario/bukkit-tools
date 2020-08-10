package clepto.bukkit;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@Getter
public class DynamicItem {

	private static final Pattern PATTERN = Pattern.compile("<([A-Za-z0-9_-]+)>");

	private final ItemStack base;
	private final ItemMeta meta;
	private final Map<String, String> placeholders = new HashMap<>();

	public DynamicItem(ItemStack staticItem) {
		this.base = staticItem;
		this.meta = staticItem.getItemMeta();
	}

	public DynamicItem fill(String key, String value) {
		placeholders.put(key, value);
		return this;
	}

	public ItemStack forceRender() {
		ItemMeta meta = this.meta.clone();

		if (!meta.hasDisplayName()) return base;

		meta.setDisplayName(render(meta.getDisplayName()));
		if (!meta.hasLore()) {
			base.setItemMeta(meta);
			return base;
		}
		List<String> list = new ArrayList<>();
		for (String s : meta.getLore()) list.add(render(s));
		meta.setLore(list);

		base.setItemMeta(meta);
		return base;

	}

	public ItemStack render() {
		if (placeholders.isEmpty()) return base;
		return forceRender();
	}

	// ToDo: Optimize item rendering
	private String render(String line) {
		if (!line.contains("<")) return line;

		try {
			List<String> payload = new ArrayList<>();
			String[] split = PATTERN.split(line, -1);

			Matcher matcher = PATTERN.matcher(line);
			while (matcher.find()) {
				String key = matcher.group(1);
				String value = placeholders.putIfAbsent(key, "???");
				payload.add(value == null ? "<" + key + ">" : value);
			}

			StringBuilder builder = new StringBuilder();
			builder.append(split[0]);
			int i = 1;

			for (String s : payload) {
				builder.append(s).append(split[i++]);
			}

			return builder.toString();
		} catch (Exception exception) {
			System.out.println("Error while rendering line " + line);
			return line;
		}
	}

}
