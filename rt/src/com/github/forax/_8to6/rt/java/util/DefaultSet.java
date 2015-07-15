package com.github.forax._8to6.rt.java.util;

import java.util.Set;

import com.github.forax._8to6.rt.java.util.function.Predicate;
import com.github.forax._8to6.rt.java.util.stream.Stream;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls;

public class DefaultSet {
  public static <T> Stream<T> stream(Set<T> set) {
    return StreamImpls.fromIterable(set);
  }
  
  public static <T> Stream<T> parallelStream(Set<T> set) {
    return StreamImpls.fromIterable(set);
  }
  
  public static <T> boolean removeIf(Set<T> list, Predicate<? super T> filter) {
    return DefaultCollection.removeIf(list, filter);
  }
}
