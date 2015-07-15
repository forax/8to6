package com.github.forax._8to6.rt.java.util.stream;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.github.forax._8to6.rt.java.util.Arrays;

public class DefaultStream {
  public static<T> Stream<T> empty() {
    return StreamImpls.fromReducer((initial, test, fun) -> initial);
  }

  public static<T> Stream<T> of(T t) {
    return StreamImpls.fromReducer((initial, test, fun) -> {
      if (test.getAsBoolean()) {
        initial = fun.apply(initial, t);
      }
      return initial;
    });
  }

  @SafeVarargs
  public static<T> Stream<T> of(T... values) {
    return Arrays.stream(values, 0, values.length);
  }

  public static<T> Stream<T> iterate(T seed, UnaryOperator<T> f) {
    f.getClass();
    return StreamImpls.fromReducer((initial, test, fun) -> {
      T value = seed;
      while(test.getAsBoolean()) {
        initial = fun.apply(initial, value);
        value = f.apply(value);
      }
      return initial;
    });
  }

  public static<T> Stream<T> generate(Supplier<T> s) {
    s.getClass();
    return StreamImpls.fromReducer((initial, test, fun) -> {
      while(test.getAsBoolean()) {
        initial = fun.apply(initial, s.get());
      }
      return initial;
    });
  }

  public static <T> Stream<T> concat(Stream<? extends T> a, Stream<? extends T> b) {
    a.getClass();
    b.getClass();
    return DefaultStream.of(a, b).flatMap(x -> x);  //FIXME x -> x
  }
}
