package com.github.forax._8to6.rt.java.util;

import java.util.Map;

import com.github.forax._8to6.rt.java.util.function.BiConsumer;
import com.github.forax._8to6.rt.java.util.function.BiFunction;
import com.github.forax._8to6.rt.java.util.function.Function;

/**
 * Default methods of java.util.Map.
 */
public class DefaultMap {
  static <K, V> V getOrDefault(Map<K, V> map, Object key, V defaultValue) {
    V value = map.get(key);
    if (value != null) {
      return value;
    }
    if (map.containsKey(key)) {
      return null;
    }
    return defaultValue;
  }

  static <K, V> void forEach(Map<K, V> map, BiConsumer<? super K, ? super V> action) {
    action.getClass();
    for (Map.Entry<K, V> entry: map.entrySet()) {
      action.accept(entry.getKey(), entry.getValue());
    }
  }

  static <K, V> void replaceAll(Map<K, V> map, BiFunction<? super K, ? super V, ? extends V> function) {
    function.getClass();
    for (Map.Entry<K, V> entry: map.entrySet()) {
      entry.setValue(function.apply(entry.getKey(), entry.getValue()));
    }
  }

  static <K, V> V putIfAbsent(Map<K, V> map, K key, V value) {
    V oldValue = map.get(key);
    if (oldValue != null) {
      return oldValue;
    }
    map.put(key, value);
    return null;
  }

  static <K, V> boolean remove(Map<K, V> map, Object key, Object value) {
    Object currentValue = map.get(key);
    if (currentValue != null) {
      if (currentValue.equals(value)) {
        map.remove(key);
        return true;
      }
      return false;
    }
    if (value != null || !map.containsKey(key)) {
      return false;
    }
    map.remove(key);
    return true;
  }

  static <K, V> boolean replace(Map<K, V> map, K key, V oldValue, V newValue) {
    Object currentValue = map.get(key);
    if (currentValue != null) {
      if (currentValue.equals(oldValue)) {
        map.put(key, newValue);
        return true;
      }
      return false;
    }
    if (oldValue != null || !map.containsKey(key)) {
      return false;
    }
    map.put(key, newValue);
    return true;
  }

  static <K, V> V replace(Map<K, V> map, K key, V value) {
    if (map.containsKey(key)) {
      return map.put(key, value);
    }
    return null;
  }

  static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
    mappingFunction.getClass();
    V currentValue = map.get(key);
    if (currentValue != null) {
      return currentValue;
    }
    V newValue = mappingFunction.apply(key);
    if (newValue != null) {
      map.put(key, newValue);
      return newValue;
    }
    return null;
  }

  static <K, V> V computeIfPresent(Map<K, V> map, K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    remappingFunction.getClass();
    V oldValue = map.get(key);
    if (oldValue == null) {
      return null;
    }
    V newValue = remappingFunction.apply(key, oldValue);
    if (newValue != null) {
      map.put(key, newValue);
      return newValue;
    }
    map.remove(key);
    return null;
  }

  static <K, V> V compute(Map<K, V> map, K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    remappingFunction.getClass();
    V oldValue = map.get(key);
    V newValue = remappingFunction.apply(key, oldValue);
    if (newValue != null) {
      map.put(key, newValue);
      return newValue;
    }
    map.remove(key);
    return null;
  }

  static <K, V> V merge(Map<K, V> map, K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    value.getClass();
    remappingFunction.getClass();
    V oldValue = map.get(key);
    V newValue = (oldValue == null)? value: remappingFunction.apply(oldValue, value);
    if (newValue != null) {
      map.put(key, newValue);
      return newValue;
    }
    map.remove(key);
    return null;
  }
}
