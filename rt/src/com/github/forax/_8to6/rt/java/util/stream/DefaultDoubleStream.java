package com.github.forax._8to6.rt.java.util.stream;

import com.github.forax._8to6.rt.java.util.Arrays;
import com.github.forax._8to6.rt.java.util.function.DoubleSupplier;
import com.github.forax._8to6.rt.java.util.function.DoubleUnaryOperator;
import com.github.forax._8to6.rt.java.util.stream.StreamImpls.DoubleStreamImpl;

public class DefaultDoubleStream {
  public static DoubleStream empty() {
    return new DoubleStreamImpl(DefaultStream.empty());
  }

  public static DoubleStream of(double t) {
    return new DoubleStreamImpl(DefaultStream.of(t));
  }

  public static DoubleStream of(double... values) {
    return Arrays.stream(values, 0, values.length);
  }

  public static DoubleStream iterate(final double seed, final DoubleUnaryOperator f) {
    return new DoubleStreamImpl(DefaultStream.iterate(seed, f::applyAsDouble));
  }

  public static DoubleStream generate(DoubleSupplier s) {
    return new DoubleStreamImpl(DefaultStream.generate(s::getAsDouble));
  }

  public static DoubleStream concat(DoubleStream a, DoubleStream b) {
    a.getClass();
    b.getClass();
    return DefaultStream.of(a, b).flatMapToDouble(x -> x); //FIXME x -> x
  }
}
