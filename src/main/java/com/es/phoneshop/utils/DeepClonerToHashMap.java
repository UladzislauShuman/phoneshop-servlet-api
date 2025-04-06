package com.es.phoneshop.utils;

import com.es.phoneshop.model.exceptions.CloneException;

import java.util.HashMap;
import java.util.Map;

public class DeepClonerToHashMap {

    private static final String EXCEPTION_NOT_IMPLEMENT_CLONEABLE = "Object of type %s does not properly implement Cloneable: %s";
    private static final String METHOD_CLONE = "clone";

    public static <K, V> HashMap<K, V> deepCopyOnlyValues(Map<K, V> map) {
        return deepCopy(map, false, true);
    }

    public static <K, V> HashMap<K, V> deepCopyKeysAndValues(Map<K, V> map) {
        return deepCopy(map, true, true);
    }

    private static <K, V> HashMap<K, V> deepCopy(Map<K, V> map, boolean cloneKeys, boolean cloneValues) {
        if (map == null) {
            return null;
        }

        HashMap<K, V> deepCopy = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = cloneKeys ? cloneObject(entry.getKey()) : entry.getKey();
            V value = cloneValues ? cloneObject(entry.getValue()) : entry.getValue();
            deepCopy.put(key, value);
        }
        return deepCopy;
    }

    private static <T> T cloneObject(T object)  {
        if (object == null) {
            return null;
        }
        try {
            return (T) object.getClass().getMethod(METHOD_CLONE).invoke(object);
        } catch (Exception e) {
            throw new CloneException(String.format(EXCEPTION_NOT_IMPLEMENT_CLONEABLE, object.getClass().getName(), e.getMessage()), e);
        }
    }
}
