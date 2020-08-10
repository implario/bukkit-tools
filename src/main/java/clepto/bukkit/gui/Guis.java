package clepto.bukkit.gui;

import clepto.bukkit.Lemonade;
import clepto.bukkit.YML;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class Guis {

	public static final Map<String, Gui> registry = new HashMap<>();
	public static final Map<String, Itemizer> itemizers = new HashMap<>();

	public static void loadGuis(ConfigurationSection guisConfig) {
		for (String id : guisConfig.getKeys(false)) {
			ConfigurationSection config = guisConfig.getConfigurationSection(id);
			String title = config.getString("title");
			String layout = config.getString("layout")
					.replace("\n", "")
					.replace("\r", "");
			Gui gui = new Gui(title, layout);
			List<ConfigurationSection> buttons = YML.getList(config, "buttons");


			Set<Character> uniques = new HashSet<>();
			Set<Character> duplicates = buttons.stream().map(b -> b.getString("char").charAt(0))
					.filter(e -> !uniques.add(e))
					.collect(Collectors.toSet());

			for (ConfigurationSection button : buttons) {
				char c = button.getString("char").charAt(0);
				String nativeStr = button.getString("native");
				String info = button.getString("info");
				String lmbCommand = button.getString("left-click");
				String rmbCommand = button.getString("right-click");
				Lemonade lem = Lemonade.parse(button);
				SlotData data = new SlotData(nativeStr, lem, commandConfig(lmbCommand), commandConfig(rmbCommand), info);
				gui.add(c, data, !duplicates.contains(c));
			}
			registry.put(id, gui);
		}
	}

	private static String[] commandConfig(String config) {
		return config == null ? null : Arrays.stream(config.split("\n"))
				.map(s -> s.startsWith("/") ? s.substring(1) : s)
				.toArray(String[]::new);
	}

	public static void registerItemizer(String key, Itemizer itemizer) {
		itemizers.put(key, itemizer);
	}
}
