package com.github.forax._8to6.rt.java.util;

import java.util.NoSuchElementException;

import com.github.forax._8to6.rt.java.util.function.DoubleConsumer;
import com.github.forax._8to6.rt.java.util.function.DoubleSupplier;
import com.github.forax._8to6.rt.java.util.function.Supplier;

public final class OptionalDouble {
  private static final OptionalDouble EMPTY = new OptionalDouble(false, 0.0);

  private final boolean isPresent;
  private final double value;

  private OptionalDouble(boolean isPresent, double value) {
    this.isPresent = isPresent;
    this.value = value;
  }

  public static OptionalDouble empty() {
    return EMPTY;
  }

  public static OptionalDouble of(double value) {
    return new OptionalDouble(true, value);
  }

  public double getAsDouble() {
    if (!isPresent) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  public boolean isPresent() {
    return isPresent;
  }

  public void ifPresent(DoubleConsumer consumer) {
    if (isPresent) {
      consumer.accept(value);
    }
  }

  public double orElse(double other) {
    return isPresent? value: other;
  }

  public double orElseGet(DoubleSupplier other) {
    return isPresent? value: other.getAsDouble();
  }


  public<X extends Throwable> double orElseThrow(Supplier<X> supplier) throws X {
    if (isPresent) {
      return value;
    }
    throw supplier.get();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof OptionalDouble)) {
      return false;
    }
    OptionalDouble optional = (OptionalDouble)obj;
    return isPresent == optional.isPresent && value == optional.value;
  }

  @Override
  public int hashCode() {
    long val = Double.doubleToRawLongBits(value);
    return isPresent? (int)(val ^ (val >>> 32)): 0;
  }

  @Override
  public String toString() {
    return isPresent? "OptionalDouble[" + value + ']': "OptionalDouble.empty";
  }
}
