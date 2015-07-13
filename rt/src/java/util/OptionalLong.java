package java.util;

import java.util.function.LongSupplier;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

public final class OptionalLong {
  private static final OptionalLong EMPTY = new OptionalLong(false, 0);

  private final boolean isPresent;
  private final long value;

  private OptionalLong(boolean isPresent, long value) {
    this.isPresent = isPresent;
    this.value = value;
  }

  public static OptionalLong empty() {
    return EMPTY;
  }

  public static OptionalLong of(long value) {
    return new OptionalLong(true, value);
  }

  public long getAsLong() {
    if (!isPresent) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  public boolean isPresent() {
    return isPresent;
  }

  public void ifPresent(LongConsumer consumer) {
    if (isPresent) {
      consumer.accept(value);
    }
  }

  public long orElse(long other) {
    return isPresent? value: other;
  }

  public long orElseGet(LongSupplier other) {
    return isPresent? value: other.getAsLong();
  }


  public<X extends Throwable> long orElseThrow(Supplier<X> supplier) throws X {
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
    if (!(obj instanceof OptionalLong)) {
      return false;
    }
    OptionalLong optional = (OptionalLong)obj;
    return isPresent == optional.isPresent && value == optional.value;
  }

  @Override
  public int hashCode() {
    return isPresent? (int)(value ^ (value >>> 32)): 0;
  }

  @Override
  public String toString() {
    return isPresent? "OptionalLong[" + value + ']': "OptionalLong.empty";
  }
}
