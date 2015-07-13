package java.util.function;

public interface BiFunction<T, U, R> {
  public R apply(T element1, U element2);
}
