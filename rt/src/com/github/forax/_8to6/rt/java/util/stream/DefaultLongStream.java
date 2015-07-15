package com.github.forax._8to6.rt.java.util.stream;

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
    //FIXME Arrays.stream
    return new LongStreamImpl(StreamImpls.streamImpl((initial, test, fun) -> {
      int length = values.length;
      for(int i = 0; i < length && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, values[i]);
      }
      return initial;
    }));
  }

  public static LongStream iterate(final long seed, final LongUnaryOperator f) {
    return new LongStreamImpl(DefaultStream.iterate(seed, f::applyAsLong));
  }

  public static LongStream generate(LongSupplier s) {
    return new LongStreamImpl(DefaultStream.generate(s::getAsLong));
  }


  public static LongStream range(long startInclusive, long endExclusive) {
    return new LongStreamImpl(StreamImpls.streamImpl((initial, test, fun) -> {
      for(long i = startInclusive; i < endExclusive && test.getAsBoolean(); i++) {
        initial = fun.apply(initial, i);
      }
      return initial;
    }));
  }

  public static LongStream rangeClosed(long startInclusive, long endInclusive) {
    return new LongStreamImpl(StreamImpls.streamImpl((initial, test, fun) -> {
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
