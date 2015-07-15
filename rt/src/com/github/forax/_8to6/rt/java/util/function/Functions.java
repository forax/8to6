package com.github.forax._8to6.rt.java.util.function;

public class Functions {
  public static <T> Function<T,T> identity() {
    return x -> x;
  }
}
