package com.github.forax._8to6.rt.java.util.stream;

public interface Collector<T, A, R> {
  public R doCollect(Stream<T> stream);
}