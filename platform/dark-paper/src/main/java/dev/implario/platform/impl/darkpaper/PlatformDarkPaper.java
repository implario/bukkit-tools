package dev.implario.platform.impl.darkpaper;

import dev.implario.bukkit.platform.Platform;
import dev.implario.bukkit.platform.Platforms;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlatformDarkPaper implements Platform {

    public static PlatformDarkPaper use() {
        PlatformDarkPaper platform = new PlatformDarkPaper();
        Platforms.set(platform);
        System.setProperty("dev.implario.bukkit.platformclass", PlatformDarkPaper.class.getName());
        return platform;
    }

    @Override
    public org.bukkit.inventory.ItemStack createItemStack(Material material, int amount, int data, Map<String, Object> nbt) {

        ItemStack item = new ItemStack(CraftMagicNumbers.getItem(material), amount, data);

        if (!nbt.isEmpty()) {
            item.tag = (NBTTagCompound) toNbt(nbt);
        }

        return item.asBukkitMirror();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getNbt(org.bukkit.inventory.ItemStack itemStack) {

        return (Map<String, Object>) fromNbt(itemStack.handle != null ?
                itemStack.handle.getOrCreateTag() :
                org.bukkit.inventory.ItemStack.asNMSCopy(itemStack).getOrCreateTag());
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
