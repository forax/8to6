package java.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class OptionalInt {
  private static final OptionalInt EMPTY = new OptionalInt(false, 0);

  private final boolean isPresent;
  private final int value;

  private OptionalInt(boolean isPresent, int value) {
    this.isPresent = isPresent;
    this.value = value;
  }

  public static OptionalInt empty() {
    return EMPTY;
  }

  public static OptionalInt of(int value) {
    return new OptionalInt(true, value);
  }

  public int getAsInt() {
    if (!isPresent) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  public boolean isPresent() {
    return isPresent;
  }

  public void ifPresent(IntConsumer consumer) {
    if (isPresent) {
      consumer.accept(value);
    }
  }

  public int orElse(int other) {
    return isPresent? value: other;
  }

  public int orElseGet(IntSupplier other) {
    return isPresent ? value : other.getAsInt();
  }


  public<X extends Throwable> int orElseThrow(Supplier<X> supplier) throws X {
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
    if (!(obj instanceof OptionalInt)) {
      return false;
    }
    OptionalInt optional = (OptionalInt)obj;
    return isPresent == optional.isPresent && value == optional.value;
  }

  @Override
  public int hashCode() {
    return isPresent? value: 0;
  }

  @Override
  public String toString() {
    return isPresent? "OptionalInt[" + value + ']': "OptionalInt.empty";
  }
}
