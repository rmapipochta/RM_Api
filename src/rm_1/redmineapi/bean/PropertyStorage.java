package rm_1.redmineapi.bean;

import java.util.HashMap;
import java.util.Map;

public final class PropertyStorage {
    private final Map<Property<?>, Object> map = new HashMap<>();

    public final <T> T get(Property<T> prop) {
        return prop.getType().cast(map.get(prop));
    }

    final <T> void set(Property<T> prop, T value) {
        map.put(prop, value);
    }

    public final boolean isPropertySet(Property<?> property) {
        return map.containsKey(property);
    }
}
