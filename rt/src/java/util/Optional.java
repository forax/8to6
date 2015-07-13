package java.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Optional<T> {
  private static final Optional<Object> EMPTY = new Optional<>(null);

  private final T value;

  private Optional(T value) {
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  public static<T> Optional<T> empty() {
    return (Optional<T>) EMPTY;
  }

  public static <T> Optional<T> of(T value) {
    value.getClass();
    return new Optional<>(value);
  }

  @SuppressWarnings("unchecked")
  public static <T> Optional<T> ofNullable(T value) {
    return value == null? (Optional<T>)EMPTY : new Optional<>(value);
  }

  public boolean isPresent() {
    return value != null;
  }

  public void ifPresent(Consumer<? super T> consumer) {
    if (value != null) {
      consumer.accept(value);
    }
  }

  public T get() {
    if (value != null) {
      return value;
    }
    throw new NoSuchElementException();
  }

  @SuppressWarnings("unchecked")
  public Optional<T> filter(Predicate<? super T> predicate) {
    return (value != null && predicate.test(value))? this: (Optional<T>)EMPTY; 
  }

  @SuppressWarnings("unchecked")
  public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
    return value != null? ofNullable(mapper.apply(value)): (Optional<U>)EMPTY;
  }

  @SuppressWarnings("unchecked")
  public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
    return value != null? mapper.apply(value): (Optional<U>)EMPTY;
  }

  public T orElse(T other) {
    return value != null? value: other;
  }

  public T orElseGet(Supplier<? extends T> supplier) {
    return value != null? value: supplier.get();
  }

  public <X extends Throwable> T orElseThrow(Supplier<? extends X> supplier) throws X {
    if (value != null) {
      return value;
    }
    throw supplier.get();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Optional)) {
      return false;
    }
    Optional<?> opt = (Optional<?>) obj;
    return value == null? opt.value == null: value.equals(opt.value);
  }

  @Override
  public int hashCode() {
    return value != null? value.hashCode(): 0;
  }

  @Override
  public String toString() {
    return value != null? "Optional[" + value + ']': "Optional.empty";
  }
}
