package java.util.stream;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

public interface IntStream {
  IntStream filter(IntPredicate predicate);
  
  IntStream map(IntUnaryOperator mapper);
  <U> Stream<U> mapToObj(IntFunction<? extends U> mapper);
  LongStream mapToLong(IntToLongFunction mapper);
  DoubleStream mapToDouble(IntToDoubleFunction mapper);

  IntStream flatMap(IntFunction<? extends IntStream> mapper);

  IntStream distinct();

  IntStream sorted();

  
  IntStream peek(IntConsumer action);

  
  IntStream limit(long maxSize);
  IntStream skip(long n);

  void forEach(IntConsumer action);
  void forEachOrdered(IntConsumer action);

  
  int[] toArray();
  int reduce(int identity, IntBinaryOperator op);
  OptionalInt reduce(IntBinaryOperator op);

  <R> R collect(Supplier<R> supplier,
                ObjIntConsumer<R> accumulator,
                BiConsumer<R, R> combiner);
  int sum();

  OptionalInt min();
  OptionalInt max();

  long count();

  OptionalDouble average();

  /*IntSummaryStatistics summaryStatistics();*/

  boolean anyMatch(IntPredicate predicate);
  boolean allMatch(IntPredicate predicate);
  boolean noneMatch(IntPredicate predicate);

  OptionalInt findFirst();
  OptionalInt findAny();

  LongStream asLongStream();
  DoubleStream asDoubleStream();

  Stream<Integer> boxed();
}
