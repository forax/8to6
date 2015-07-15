package com.github.forax._8to6.rt.java.util;

import com.github.forax._8to6.rt.java.util.function.IntFunction;
import com.github.forax._8to6.rt.java.util.function.IntToDoubleFunction;
import com.github.forax._8to6.rt.java.util.function.IntToLongFunction;
import com.github.forax._8to6.rt.java.util.function.IntUnaryOperator;
import com.github.forax._8to6.rt.java.util.stream.DoubleStream;
import com.github.forax._8to6.rt.java.util.stream.IntStream;
import com.github.forax._8to6.rt.java.util.stream.LongStream;
import com.github.forax._8to6.rt.java.util.stream.Stream;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls;

public class Arrays {
  public static <T> void setAll(T[] array, IntFunction<? extends T> generator) {
    generator.getClass();
    int length = array.length;
    for (int i = 0; i < length; i++) {
      array[i] = generator.apply(i);
    }
  }

  public static <T> void parallelSetAll(T[] array, IntFunction<? extends T> generator) {
    setAll(array, generator);
  }

  public static void setAll(int[] array, IntUnaryOperator generator) {
    generator.getClass();
    int length = array.length;
    for (int i = 0; i < length; i++) {
      array[i] = generator.applyAsInt(i);
    }
  }

  public static void parallelSetAll(int[] array, IntUnaryOperator generator) {
    setAll(array, generator);
  }

  public static void setAll(long[] array, IntToLongFunction generator) {
    generator.getClass();
    int length = array.length;
    for (int i = 0; i < length; i++) {
      array[i] = generator.applyAsLong(i);
    }
  }

  public static void parallelSetAll(long[] array, IntToLongFunction generator) {
    setAll(array, generator);
  }

  public static void setAll(double[] array, IntToDoubleFunction generator) {
    generator.getClass();
    int length = array.length;
    for (int i = 0; i < length; i++) {
      array[i] = generator.applyAsDouble(i);
    }
  }

  public static void parallelSetAll(double[] array, IntToDoubleFunction generator) {
    setAll(array, generator);
  }

  public static <T> Stream<T> stream(T[] array) {
    return stream(array, 0, array.length);
  }

  public static <T> Stream<T> stream(T[] array, int startInclusive, int endExclusive) {
    return StreamImpls.fromReducer((initial, test, fun) -> {
      for(int i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, array[i]);
      }
      return initial;
    });
  }

  public static IntStream stream(int[] array) {
    return stream(array, 0, array.length);
  }

  public static IntStream stream(int[] array, int startInclusive, int endExclusive) {
    return StreamImpls.wrapToIntStream(StreamImpls.fromReducer((initial, test, fun) -> {
      for(int i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, array[i]);
      }
      return initial;
    }));
  }

  public static LongStream stream(long[] array) {
    return stream(array, 0, array.length);
  }

  public static LongStream stream(long[] array, int startInclusive, int endExclusive) {
    return StreamImpls.wrapToLongStream(StreamImpls.fromReducer((initial, test, fun) -> {
      for(int i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, array[i]);
      }
      return initial;
    }));
  }

  public static DoubleStream stream(double[] array) {
    return stream(array, 0, array.length);
  }

  public static DoubleStream stream(double[] array, int startInclusive, int endExclusive) {
    return StreamImpls.wrapToDoubleStream(StreamImpls.fromReducer((initial, test, fun) -> {
      for(int i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, array[i]);
      }
      return initial;
    }));
  }
}
