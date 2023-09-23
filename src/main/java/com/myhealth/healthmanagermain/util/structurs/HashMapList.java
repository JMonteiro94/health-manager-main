package com.myhealth.healthmanagermain.util.structurs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HashMapList<T, E> {

  private final HashMap<T, ArrayList<E>> map = new HashMap<>();

  public void put(T key, E item) {
    if (!map.containsKey(key)) {
      map.put(key, new ArrayList<E>());
    }
    map.get(key).add(item);
  }

  public void put(T key, List<E> items) {
    map.put(key, (ArrayList<E>) items);
  }

  public List<E> get(T key) {
    return map.get(key);
  }

  public boolean containsKey(T key) {
    return map.containsKey(key);
  }

  public boolean containsKeyValue(T key, E value) {
    ArrayList<E> list = (ArrayList<E>) get(key);
    if (list == null) {
      return false;
    }
    return list.contains(value);
  }

  public Set<T> keySet() {
    return map.keySet();
  }

  @Override
  public String toString() {
    return map.toString();
  }
}
