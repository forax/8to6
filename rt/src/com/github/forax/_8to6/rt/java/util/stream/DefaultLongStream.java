package com.github.forax._8to6.rt.java.util.stream;

import com.github.forax._8to6.rt.java.util.Arrays;
import com.github.forax._8to6.rt.java.util.function.LongSupplier;
import com.github.forax._8to6.rt.java.util.function.LongUnaryOperator;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls.LongStreamImpl;

public class DefaultLongStream {
  public static LongStream empty() {
    return new LongStreamImpl(DefaultStream.empty());
  }

  public static LongStream of(long t) {
    return new LongStreamImpl(DefaultStream.of(t));
  }

  public static LongStream of(long... values) {
    return Arrays.stream(values, 0, values.length);
  }

  public static LongStream iterate(final long seed, final LongUnaryOperator f) {
    return new LongStreamImpl(DefaultStream.iterate(seed, f::applyAsLong));
  }

  public static LongStream generate(LongSupplier s) {
    return new LongStreamImpl(DefaultStream.generate(s::getAsLong));
  }


  public static LongStream range(long startInclusive, long endExclusive) {
    return new LongStreamImpl(StreamImpls.fromReducer((initial, test, fun) -> {
      for(long i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, i);
      }
      return initial;
    }));
  }

  public static LongStream rangeClosed(long startInclusive, long endInclusive) {
    return new LongStreamImpl(StreamImpls.fromReducer((initial, test, fun) -> {
      for(long i = startInclusive; i <= endInclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, i);
      }
      return initial;
    }));
  }

  public static LongStream concat(LongStream a, LongStream b) {
    a.getClass();
    b.getClass();
    return DefaultStream.of(a, b).flatMapToLong(x -> x); //FIXME x -> x
  }
}
