package com.github.forax._8to6.rt.java.util.stream;

import java.util.function.IntSupplier;

import com.github.forax._8to6.rt.java.util.function.IntUnaryOperator;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls.IntStreamImpl;

public class DefaultIntStream {
  public static IntStream empty() {
    return new IntStreamImpl(DefaultStream.empty());
  }

  public static IntStream of(int t) {
    return new IntStreamImpl(DefaultStream.of(t));
  }

  public static IntStream of(int... values) {
    //FIXME Arrays.stream
    return new IntStreamImpl(StreamImpls.streamImpl((initial, test, fun) -> {
      int length = values.length;
      for(int i = 0; i < length && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, values[i]);
      }
      return initial;
    }));
  }

  public static IntStream iterate(int seed, IntUnaryOperator f) {
    return new IntStreamImpl(DefaultStream.iterate(seed, f::applyAsInt));
  }

  public static IntStream generate(IntSupplier s) {
    return new IntStreamImpl(DefaultStream.generate(s::getAsInt));
  }

  public static IntStream range(int startInclusive, int endExclusive) {
    return new IntStreamImpl(StreamImpls.streamImpl((initial, test, fun) -> {
      for(int i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, i);
      }
      return initial;
    }));
  }

  public static IntStream rangeClosed(int startInclusive, int endInclusive) {
    return new IntStreamImpl(StreamImpls.streamImpl((initial, test, fun) -> {
      for(int i = startInclusive; i <= endInclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, i);
      }
      return initial;
    }));
  }

  public static IntStream concat(IntStream a, IntStream b) {
    a.getClass();
    b.getClass();
    return DefaultStream.of(a, b).flatMapToInt(x -> x); //FIXME x -> x
  }
}
