package com.github.forax._8to6.rt.java.util.stream;

import com.github.forax._8to6.rt.java.util.OptionalDouble;
import com.github.forax._8to6.rt.java.util.OptionalLong;
import com.github.forax._8to6.rt.java.util.function.BiConsumer;
import com.github.forax._8to6.rt.java.util.function.LongBinaryOperator;
import com.github.forax._8to6.rt.java.util.function.LongConsumer;
import com.github.forax._8to6.rt.java.util.function.LongFunction;
import com.github.forax._8to6.rt.java.util.function.LongPredicate;
import com.github.forax._8to6.rt.java.util.function.LongToDoubleFunction;
import com.github.forax._8to6.rt.java.util.function.LongToIntFunction;
import com.github.forax._8to6.rt.java.util.function.LongUnaryOperator;
import com.github.forax._8to6.rt.java.util.function.ObjLongConsumer;
import com.github.forax._8to6.rt.java.util.function.Supplier;

public interface LongStream {
  LongStream filter(LongPredicate predicate);

  LongStream map(LongUnaryOperator mapper);
  <U> Stream<U> mapToObj(LongFunction<? extends U> mapper);
  IntStream mapToInt(LongToIntFunction mapper);
  DoubleStream mapToDouble(LongToDoubleFunction mapper);
  LongStream flatMap(LongFunction<? extends LongStream> mapper);

  LongStream distinct();
  LongStream sorted();

  LongStream peek(LongConsumer action);

  LongStream limit(long maxSize);
  LongStream skip(long n);

  void forEach(LongConsumer action);
  void forEachOrdered(LongConsumer action);

  long[] toArray();

  long reduce(long identity, LongBinaryOperator op);
  OptionalLong reduce(LongBinaryOperator op);

  <R> R collect(Supplier<R> supplier,
                ObjLongConsumer<R> accumulator,
                BiConsumer<R, R> combiner);

  long sum();

  OptionalLong min();
  OptionalLong max();

  long count();

  OptionalDouble average();

  /*LongSummaryStatistics summaryStatistics();*/

  boolean anyMatch(LongPredicate predicate);
  boolean allMatch(LongPredicate predicate);
  boolean noneMatch(LongPredicate predicate);

  OptionalLong findFirst();
  OptionalLong findAny();

  DoubleStream asDoubleStream();
  Stream<Long> boxed();
}
