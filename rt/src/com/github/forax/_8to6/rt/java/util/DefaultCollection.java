package com.github.forax._8to6.rt.java.util;

import java.util.Collection;
import java.util.Iterator;

import com.github.forax._8to6.rt.java.util.function.Predicate;
import com.github.forax._8to6.rt.java.util.stream.Stream;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls;

public class DefaultCollection {
  public static <T> Stream<T> stream(Collection<T> collection) {
    return StreamImpls.fromIterable(collection);
  }

  public static <T> Stream<T> parallelStream(Collection<T> collection) {
    return StreamImpls.fromIterable(collection);
  }
  
  public static <T> boolean removeIf(Collection<T> collection, Predicate<? super T> filter) {
    filter.getClass();
    boolean changed = false;
    Iterator<T> it = collection.iterator();
    while (it.hasNext()) {
      if (filter.test(it.next())) {
        it.remove();
        changed = true;
      }
    }
    return changed;
  }
}
