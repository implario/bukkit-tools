package dev.implario.bukkit.config;

import com.google.gson.*;
import dev.implario.bukkit.reflect.Reflection;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

public class YamlTools {

    private static final MethodHandle mapGetter = Reflection.getter(MemorySection.class, "map", Map.class);

    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(HolderMessage.class,
            (JsonDeserializer<HolderMessage>) (json, type, ctx) -> {
                if (json.isJsonNull()) return new HolderMessage(Collections.singletonList("???"));
                List<String> lines = new ArrayList<>();
                if (json.isJsonArray()) json.getAsJsonArray().forEach(s -> lines.add(s.getAsString()));
                else lines.addAll(Arrays.asList(json.getAsString().split("\n")));
                return new HolderMessage(lines);
            }).create();

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

    public static <T> T readYaml(ConfigurationSection section, Class<T> type, Gson gson) {
        JsonElement jsonElement = toJsonElement(section);
        return gson.fromJson(jsonElement, type);
    }

    @SneakyThrows
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
            return toJsonElement((Map<?, ?>) mapGetter.invoke((MemorySection) o));

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
