package net.techcable.tacospigot.function;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Map;
import java.util.function.ObjIntConsumer;
import javax.annotation.ParametersAreNonnullByDefault;

import net.techcable.tacospigot.ArrayMap;

import static com.google.common.base.Preconditions.*;

@ParametersAreNonnullByDefault
public class FunctionalMaps {
    public static <V> void forEachPrimitive(Int2ObjectMap<V> map, ObjIntConsumer<V> action) {
        if (map instanceof ArrayMap) {
            //noinspection unchecked - ur mums unchecked!
            ((ArrayMap) checkNotNull(map, "Null map")).forEachPrimitive(action);
        } else {
            defaultForEachPrimitive(map, action);
        }
    }

    private static <V> void defaultForEachPrimitive(Int2ObjectMap<V> map, ObjIntConsumer<V> action) {
        for (Map.Entry<Integer, V> entry : map.entrySet()) {
            action.accept(entry.getValue(), entry.getKey());
        }
    }
}