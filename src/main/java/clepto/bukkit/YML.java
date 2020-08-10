package clepto.bukkit;

import com.google.gson.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings ({"unchecked", "rawtypes"})
public class YML {

	public static YamlConfiguration readComplexConfig(File... files) {
		return readComplexConfig(Arrays.asList(files));
	}

	public static YamlConfiguration readComplexConfig(Iterable<File> files) {
		if (files == null) return null;
		Yaml yaml = new Yaml();
		List<Map<?, ?>> simpleConfigs = new ArrayList<>();
		for (File file : files) {
			if (!file.getName().endsWith(".yml")) continue;
			try (BufferedReader input = new BufferedReader(new FileReader(file))) {
				StringBuilder builder = new StringBuilder();

				String line;
				while ((line = input.readLine()) != null) {
					builder.append(line);
					builder.append('\n');
				}

				simpleConfigs.add(yaml.load(builder.toString()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		YamlConfiguration config = new YamlConfiguration();
		config.convertMapsToSections((Map<?, ?>) merge("config", simpleConfigs.toArray()), config);
		return config;
	}

	private enum Type {
		MAP,
		LIST
	}

	public static Object merge(String currentPath, Object... objects) {
		if (objects == null || objects.length == 0) return null;
		if (objects.length == 1) return objects[0];

		Type type = null;
		for (Object object : objects) {
			if (object == null) continue;
			Type t;
			if (object instanceof Map) t = Type.MAP;
			else if (object instanceof Collection) t = Type.LIST;
			else throw new InvalidConfigException(currentPath + " has multiple primitive values");

			if (type != null && type != t)
				throw new InvalidConfigException(currentPath + " has incompatible types inside: " + t + " and " + type);
			type = t;
		}

		if (type == Type.MAP) {
			Map<String, List> map = new HashMap<>();
			for (Object object : objects) {
				Map<?, ?> data = (Map<?, ?>) object;
				for (Map.Entry<?, ?> entry : data.entrySet()) {
					Object key = entry.getKey();
					String keyStr = String.valueOf(key);
					Object value = entry.getValue();
					map.computeIfAbsent(keyStr, e -> new ArrayList()).add(value);
				}
			}
			Map<String, Object> section = new HashMap<>();
			for (Map.Entry<String, List> e : map.entrySet()) {
				section.put(e.getKey(), merge(currentPath + '.' + e.getKey(), e.getValue().toArray()));
			}
			return section;
		}

		if (type == Type.LIST) {
			List list = new ArrayList();
			for (Object object : objects) list.addAll((List<?>) object);
			return list;
		}

		throw new InvalidConfigException("wtf");

	}

	public static List<ConfigurationSection> getList(ConfigurationSection section, String key) {
		return getList(section, key, yml -> yml);
	}

	public static <T> List<T> getList(ConfigurationSection section, String key, Function<ConfigurationSection, T> deserializer) {
		List<Map<?, ?>> list = section.getMapList(key);
		if (list == null) return null;
		List<T> objects = new ArrayList<>();
		int id = 0;
		for (Map<?, ?> map : list) {
			objects.add(deserializer.apply(section.createSection(section.getCurrentPath() + "." + id++, map)));
		}
		return objects;
	}

	public static JsonElement toJsonElement(Object o) {

		//NULL => JsonNull
		if (o == null)
			return JsonNull.INSTANCE;

		// Collection => JsonArray
		if (o instanceof Collection) {
			JsonArray array = new JsonArray();
			for (Object childObj : (Collection<?>) o)
				array.add(toJsonElement(childObj));
			return array;
		}

		// Array => JsonArray
		if (o.getClass().isArray()) {
			JsonArray array = new JsonArray();

			int length = Array.getLength(array);
			for (int i = 0; i < length; i++)
				array.add(toJsonElement(Array.get(array, i)));

			return array;
		}

		if (o instanceof MemorySection)
			return toJsonElement(((MemorySection) o).map);

		// Map => JsonObject
		if (o instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) o;

			JsonObject jsonObject = new JsonObject();
			for (final Map.Entry<?, ?> entry : map.entrySet()) {
				final String name = String.valueOf(entry.getKey());
				final Object value = entry.getValue();
				jsonObject.add(name, toJsonElement(value));
			}

			return jsonObject;
		}

		// everything else => JsonPrimitive
		if (o instanceof String)
			return new JsonPrimitive((String) o);
		if (o instanceof Number)
			return new JsonPrimitive((Number) o);
		if (o instanceof Character)
			return new JsonPrimitive((Character) o);
		if (o instanceof Boolean)
			return new JsonPrimitive((Boolean) o);

		// otherwise.. string is a good guess
		return new JsonPrimitive(String.valueOf(o));
	}

}
