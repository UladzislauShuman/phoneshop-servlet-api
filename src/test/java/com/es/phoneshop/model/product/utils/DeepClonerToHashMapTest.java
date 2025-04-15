
package com.es.phoneshop.model.product.utils;

import com.es.phoneshop.utils.DeepClonerToHashMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DeepClonerToHashMapTest {

    public static final String VALUE_1 = "value1";
    public static final String VALUE_2 = "value2";
    public static final String KEY_1 = "key1";
    public static final String KEY_2 = "key2";

    private static final CloneableObject obj1 = new CloneableObject(VALUE_1);
    private static final CloneableObject obj2 = new CloneableObject(VALUE_2);
    private static final CloneableString key1 = new CloneableString(KEY_1);
    private static final CloneableString key2 = new CloneableString(KEY_2);
    public static final String TEST = "test";

    @Test
    void deepCopyOnlyValues_nullMap() {
        assertNull(DeepClonerToHashMap.deepCopyOnlyValues(null));
    }

    @Test
    void deepCopyKeysAndValues_nullMap() {
        assertNull(DeepClonerToHashMap.deepCopyKeysAndValues(null));
    }

    @Test
    void deepCopyKeysAndValues_mapWithKeyIsNull() {
        Map<CloneableString, CloneableObject> originalMap = new HashMap<>();
        originalMap.put(null, new CloneableObject(TEST));

        Map<CloneableString, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyKeysAndValues(originalMap);

        assertNotNull(deepCopy);
        assertEquals(1, deepCopy.size());
    }

    @Test
    void deepCopyKeysAndValues_mapWithValueIsNull() {
        Map<CloneableString, CloneableObject> originalMap = new HashMap<>();
        originalMap.put(new CloneableString(TEST), null);

        Map<CloneableString, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyKeysAndValues(originalMap);

        assertNotNull(deepCopy);
        assertEquals(1, deepCopy.size());
    }

    @Test
    void deepCopyOnlyValues_emptyMap() {
        Map<String, CloneableObject> emptyMap = new HashMap<>();
        Map<String, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyOnlyValues(emptyMap);
        assertNotNull(deepCopy);
        assertTrue(deepCopy.isEmpty());
    }

    @Test
    void deepCopyOnlyValues_mapWithValues() {
        // инициализируем карту
        Map<String, CloneableObject> originalMap = new HashMap<>();
        originalMap.put(KEY_1, obj1);
        originalMap.put(KEY_2, obj2);

        // делаем глубокое копирование
        Map<String, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyOnlyValues(originalMap);

        // проверяем
        assertNotNull(deepCopy);
        assertEquals(originalMap.size(), deepCopy.size());
        assertNotSame(originalMap, deepCopy);

        CloneableObject copiedObj1 = deepCopy.get(KEY_1);
        CloneableObject copiedObj2 = deepCopy.get(KEY_2);

        assertNotNull(copiedObj1);
        assertNotNull(copiedObj2);
        assertNotSame(obj1, copiedObj1);
        assertNotSame(obj2, copiedObj2);
        assertEquals(obj1.getValue(), copiedObj1.getValue());
        assertEquals(obj2.getValue(), copiedObj2.getValue());
    }

    @Test
    void deepCopyOnlyValues_mapWithValueIsNull() {
        Map<String, CloneableObject> originalMap = new HashMap<>();
        originalMap.put(KEY_1, null);

        Map<String, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyOnlyValues(originalMap);

        assertNotNull(deepCopy);
        assertEquals(1, deepCopy.size());
    }

    @Test
    void deepCopyKeysAndValues_emptyMap() {
        Map<CloneableString, CloneableObject> emptyMap = new HashMap<>();
        Map<CloneableString, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyKeysAndValues(emptyMap);
        assertNotNull(deepCopy);
        assertTrue(deepCopy.isEmpty());
    }

    @Test
    void deepCopyKeysAndValues_mapWithValues() {
        // инициализируем карту
        Map<CloneableString, CloneableObject> originalMap = new HashMap<>();
        originalMap.put(key1, obj1);
        originalMap.put(key2, obj2);

        // копируем
        Map<CloneableString, CloneableObject> deepCopy = DeepClonerToHashMap.deepCopyKeysAndValues(originalMap);

        // проверяем
        assertNotNull(deepCopy);
        assertEquals(originalMap.size(), deepCopy.size());
        assertNotSame(originalMap, deepCopy);

        CloneableObject copiedObj1 = deepCopy.get(key1);
        CloneableObject copiedObj2 = deepCopy.get(key2);

        assertNotSame(key1, deepCopy.keySet().stream().filter(k -> k.equals(key1)).findFirst().orElse(null));
        assertNotSame(key2, deepCopy.keySet().stream().filter(k -> k.equals(key2)).findFirst().orElse(null));

        assertNotNull(copiedObj1);
        assertNotNull(copiedObj2);
        assertNotSame(obj1, copiedObj1);
        assertNotSame(obj2, copiedObj2);
        assertEquals(obj1.getValue(), copiedObj1.getValue());
        assertEquals(obj2.getValue(), copiedObj2.getValue());
    }

    public static class CloneableObject implements Cloneable {
        private String value;

        public CloneableObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public CloneableObject clone() {
            try {
                return (CloneableObject) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class CloneableString implements Cloneable {
        private String value;

        public CloneableString(String value) {
            this.value = value;
        }

        @Override
        public CloneableString clone() {
            try {
                return (CloneableString) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CloneableString that = (CloneableString) o;
            return value != null ? value.equals(that.value) : that.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}