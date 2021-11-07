package dev.implario.bukkit.reflect;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class Reflection {

    public static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @SneakyThrows
    public static <O, F> MethodHandle getter(Class<O> ownerType, String fieldName, Class<F> fieldType) {
        Field field = ownerType.getDeclaredField(fieldName);
        field.setAccessible(true);
        return lookup.unreflectGetter(field);
    }

}
