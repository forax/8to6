package com.github.forax._8to6.rt.java.util.stream;

import com.github.forax._8to6.rt.java.util.OptionalDouble;
import com.github.forax._8to6.rt.java.util.OptionalInt;
import com.github.forax._8to6.rt.java.util.function.BiConsumer;
import com.github.forax._8to6.rt.java.util.function.IntBinaryOperator;
import com.github.forax._8to6.rt.java.util.function.IntConsumer;
import com.github.forax._8to6.rt.java.util.function.IntFunction;
import com.github.forax._8to6.rt.java.util.function.IntPredicate;
import com.github.forax._8to6.rt.java.util.function.IntToDoubleFunction;
import com.github.forax._8to6.rt.java.util.function.IntToLongFunction;
import com.github.forax._8to6.rt.java.util.function.IntUnaryOperator;
import com.github.forax._8to6.rt.java.util.function.ObjIntConsumer;
import com.github.forax._8to6.rt.java.util.function.Supplier;

public interface IntStream extends BaseStream<Integer, IntStream> {
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
