package dev.implario.platform.impl.v1_16_R3;

import dev.implario.bukkit.platform.Platform;
import dev.implario.bukkit.platform.Platforms;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;

import java.lang.invoke.MethodHandle;
import java.util.*;

import static dev.implario.bukkit.reflect.Reflection.getter;

public class Platform1_16_R3 implements Platform {

    private static final MethodHandle
            craftItemStackGetHandle = getter(CraftItemStack.class, "handle", ItemStack.class),
            nbtTagCompoundGetMap = getter(NBTTagCompound.class, "map", Map.class),
            nbtTagListGetList = getter(NBTTagList.class, "list", List.class);

    public static Platform1_16_R3 use() {
        Platform1_16_R3 platform = new Platform1_16_R3();
        Platforms.set(platform);
        System.setProperty("dev.implario.bukkit.platformclass", Platform1_16_R3.class.getName());
        return platform;
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(Material material, int amount, int data, Map<String, Object> nbt) {
        EntityPlayer player;

        ItemStack item = new ItemStack(CraftMagicNumbers.getItem(material), amount);

        if (!nbt.isEmpty()) {
            item.setTag((NBTTagCompound) toNbt(nbt));
        }

        item.setDamage(data);

        return CraftItemStack.asCraftMirror(item);
    }

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getNbt(org.bukkit.inventory.ItemStack itemStack) {

        NBTTagCompound tag;
        if (itemStack instanceof CraftItemStack) {

            ItemStack handle = (ItemStack) craftItemStackGetHandle.invokeExact((CraftItemStack) itemStack);
            tag = handle.getOrCreateTag();

        } else {
            tag = CraftItemStack.asNMSCopy(itemStack).getOrCreateTag();
        }

        return (Map<String, Object>) fromNbt(tag);
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
        if (object.getClass() == Integer.class) return NBTTagInt.a((Integer) object);
        if (object.getClass() == Double.class) return NBTTagDouble.a((Double) object);
        if (object.getClass() == Float.class) return NBTTagFloat.a((Float) object);
        if (object.getClass() == Long.class) return NBTTagLong.a((Long) object);
        if (object.getClass() == Short.class) return NBTTagShort.a((Short) object);
        if (object.getClass() == Byte.class) return NBTTagByte.a((Byte) object);
        if (object.getClass() == byte[].class) return new NBTTagByteArray((byte[]) object);
        if (object instanceof CharSequence) return NBTTagString.a(String.valueOf(object));
        return null;
    }

    @SneakyThrows
    @SuppressWarnings({"RedundantCast", "unchecked"})
    public static Object fromNbt(NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            Map<String, Object> map = new HashMap<>();
            NBTTagCompound nbtMap = (NBTTagCompound) nbt;

            Map<String, NBTBase> underlyingMap = (Map<String, NBTBase>) nbtTagCompoundGetMap.invokeExact(nbtMap);

            for (Map.Entry<String, NBTBase> entry : underlyingMap.entrySet()) {
                map.put(entry.getKey(), fromNbt(entry.getValue()));
            }
            return map;
        }
        if (nbt instanceof NBTTagList) {
            Collection<Object> collection = new ArrayList<>();
            NBTTagList nbtList = (NBTTagList) nbt;
            List<NBTBase> underlyingList = (List<NBTBase>) nbtTagListGetList.invokeExact(nbtList);
            for (NBTBase o : underlyingList) collection.add(fromNbt(o));
            return collection;
        }
        if (nbt.getClass() == NBTTagInt.class) return ((NBTTagInt) nbt).asInt();
        if (nbt.getClass() == NBTTagDouble.class) return ((NBTTagDouble) nbt).asDouble();
        if (nbt.getClass() == NBTTagFloat.class) return ((NBTTagFloat) nbt).asFloat();
        if (nbt.getClass() == NBTTagLong.class) return ((NBTTagLong) nbt).asLong();
        if (nbt.getClass() == NBTTagShort.class) return ((NBTTagShort) nbt).asShort();
        if (nbt.getClass() == NBTTagByte.class) return ((NBTTagByte) nbt).asByte();
        if (nbt.getClass() == NBTTagByteArray.class) return ((NBTTagByteArray) nbt).getBytes();
        if (nbt.getClass() == NBTTagString.class) return ((NBTTagString) nbt).asString();
        return null;
    }

}
