package com.github.forax._8to6.rt.java.util;

import java.util.List;

import com.github.forax._8to6.rt.java.util.function.Predicate;
import com.github.forax._8to6.rt.java.util.stream.Stream;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls;

public class DefaultList {
  public static <T> Stream<T> stream(List<T> list) {
    return StreamImpls.fromIterable(list);
  }
  
  public static <T> Stream<T> parallelStream(List<T> list) {
    return StreamImpls.fromIterable(list);
  }
  
  public static <T> boolean removeIf(List<T> list, Predicate<? super T> filter) {
    return DefaultCollection.removeIf(list, filter);
  }
}
