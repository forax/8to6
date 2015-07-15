package com.github.forax._8to6.rt.java.util.stream;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.github.forax._8to6.rt.java.util.Optional;
import com.github.forax._8to6.rt.java.util.OptionalDouble;
import com.github.forax._8to6.rt.java.util.OptionalInt;
import com.github.forax._8to6.rt.java.util.OptionalLong;
import com.github.forax._8to6.rt.java.util.function.BiConsumer;
import com.github.forax._8to6.rt.java.util.function.BiFunction;
import com.github.forax._8to6.rt.java.util.function.BinaryOperator;
import com.github.forax._8to6.rt.java.util.function.BooleanSupplier;
import com.github.forax._8to6.rt.java.util.function.Consumer;
import com.github.forax._8to6.rt.java.util.function.DoubleBinaryOperator;
import com.github.forax._8to6.rt.java.util.function.DoubleConsumer;
import com.github.forax._8to6.rt.java.util.function.DoubleFunction;
import com.github.forax._8to6.rt.java.util.function.DoublePredicate;
import com.github.forax._8to6.rt.java.util.function.DoubleToIntFunction;
import com.github.forax._8to6.rt.java.util.function.DoubleToLongFunction;
import com.github.forax._8to6.rt.java.util.function.DoubleUnaryOperator;
import com.github.forax._8to6.rt.java.util.function.Function;
import com.github.forax._8to6.rt.java.util.function.IntBinaryOperator;
import com.github.forax._8to6.rt.java.util.function.IntConsumer;
import com.github.forax._8to6.rt.java.util.function.IntFunction;
import com.github.forax._8to6.rt.java.util.function.IntPredicate;
import com.github.forax._8to6.rt.java.util.function.IntToDoubleFunction;
import com.github.forax._8to6.rt.java.util.function.IntToLongFunction;
import com.github.forax._8to6.rt.java.util.function.IntUnaryOperator;
import com.github.forax._8to6.rt.java.util.function.LongBinaryOperator;
import com.github.forax._8to6.rt.java.util.function.LongConsumer;
import com.github.forax._8to6.rt.java.util.function.LongFunction;
import com.github.forax._8to6.rt.java.util.function.LongPredicate;
import com.github.forax._8to6.rt.java.util.function.LongToDoubleFunction;
import com.github.forax._8to6.rt.java.util.function.LongToIntFunction;
import com.github.forax._8to6.rt.java.util.function.LongUnaryOperator;
import com.github.forax._8to6.rt.java.util.function.ObjDoubleConsumer;
import com.github.forax._8to6.rt.java.util.function.ObjIntConsumer;
import com.github.forax._8to6.rt.java.util.function.ObjLongConsumer;
import com.github.forax._8to6.rt.java.util.function.Predicate;
import com.github.forax._8to6.rt.java.util.function.Supplier;
import com.github.forax._8to6.rt.java.util.function.ToDoubleFunction;
import com.github.forax._8to6.rt.java.util.function.ToIntFunction;
import com.github.forax._8to6.rt.java.util.function.ToLongFunction;

public class StreamImpls {
  public static <T> StreamImpl<T> fromIterable(Iterable<? extends T> iterable) {
    return fromReducer((initial, test, fun) -> reduceLoop(iterable.iterator(), initial, test, fun));
  }
  
  public static <T> StreamImpl<T> fromReducer(Reducer<T> looper) {
    return new StreamImpl<>(() -> { /*empty*/ }, looper);
  }
  
  public static IntStream wrapToIntStream(StreamImpl<Integer> impl) {
    return new IntStreamImpl(impl);
  }
  public static LongStream wrapToLongStream(StreamImpl<Long> impl) {
    return new LongStreamImpl(impl);
  }
  public static DoubleStream wrapToDoubleStream(StreamImpl<Double> impl) {
    return new DoubleStreamImpl(impl);
  }
  
  // --- implementations
  
  public interface Reducer<T> {
    Object loop(Object initial, BooleanSupplier test, BiFunction<Object, ? super T, Object> fun);
  }
  
  static <T> Object reduceLoop(Iterator<? extends T> iterator, Object initial, BooleanSupplier test, BiFunction<Object, ? super T, Object> fun) {
    Object acc = initial;
    while(test.getAsBoolean() && iterator.hasNext()) {
      acc = fun.apply(acc, iterator.next());
    }
    return acc;
  }
  
  static class Counter {
    long counter;
    Counter(long counter)  { this.counter = counter; }
  }
  static class Stop implements BooleanSupplier {
    boolean stop = true;
    
    @Override
    public boolean getAsBoolean() {
      return stop;
    }
  }
  
  public static class StreamImpl<T> implements Stream<T> {
    private final Runnable closer;
    private final Reducer<T> reducer;
    
    StreamImpl(Runnable closer, Reducer<T> reducer) {
      this.closer = closer;
      this.reducer = reducer;
    }
    
    @Override
    public void forEach(Consumer<? super T> consumer) {
      reducer.loop(null, () -> true, (__, element) -> { consumer.accept(element); return null; });
    }
    @Override
    public void forEachOrdered(Consumer<? super T> consumer) {
      forEach(consumer);
    }
    
    @Override
    public Optional<T> findAny() {
      return findFirst();
    }
    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> findFirst() {
      Stop stop = new Stop();
      return (Optional<T>)reducer.loop(Optional.empty(), stop, (acc, element) -> {
        stop.stop = false;
        return Optional.of(element);
      });
    }
    
    @Override
    public Stream<T> limit(long maxSize) {
      Counter counter = new Counter(0);
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial,
            () -> test.getAsBoolean() && counter.counter < maxSize,
            (acc, element) -> {
              counter.counter++;
              return fun.apply(acc, element);
            }));
    }
    @Override
    public Stream<T> skip(long n) {
      Counter counter = new Counter(n);
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial, test, (acc, element) -> {
          if (counter.counter > 0) {
            counter.counter--;
            return acc;
          }
          return fun.apply(acc, element);
        }));
    }
    
    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
      Stop stop = new Stop();
      return (Boolean)reducer.loop(true, stop, (acc, element) -> {
        if (!predicate.test(element)) {
          stop.stop = false;
          return false;
        }
        return true;
      });
    }
    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
      return allMatch(element -> !predicate.test(element));
    }
    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
      Stop stop = new Stop();
      return (Boolean)reducer.loop(false, stop, (acc, element) -> {
        if (predicate.test(element)) {
          stop.stop = false;
          return true;
        }
        return false;
      });
    }
    
    
    @Override
    public Stream<T> peek(Consumer<? super T> action) {
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial, test, (acc, element) -> {
          action.accept(element);
          return fun.apply(acc, element);
        })
      );
    }
    
    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial, test, (acc, element) -> {
          if (predicate.test(element)) {
            return fun.apply(acc, element);
          }
          return acc;
        })
      );
    }
    
    @Override
    public <U> Stream<U> map(Function<? super T, ? extends U> mapper) {
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial, test, (acc, element) ->
          fun.apply(acc, mapper.apply(element))));
    }
    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
      return new IntStreamImpl(map(mapper::applyAsInt));
    }
    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
      return new LongStreamImpl(map(mapper::applyAsLong));
    }
    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
      return new DoubleStreamImpl(map(mapper::applyAsDouble));
    }
    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial, test, (acc, element) -> {
          Stream<? extends R> stream = mapper.apply(element);
          try(StreamImpl<? extends R> impl =  (stream instanceof StreamImpl)? 
            (StreamImpl<? extends R>)stream:
            fromIterable(stream::iterator)) {
            return impl.reducer.loop(acc, test, fun);
          }
        }));
    }
    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
      return new IntStreamImpl(flatMap(element -> mapper.apply(element).boxed()));
    }
    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
      return new LongStreamImpl(flatMap(element -> mapper.apply(element).boxed()));
    }
    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
      return new DoubleStreamImpl(flatMap(element -> mapper.apply(element).boxed()));
    }
    
    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
      return reduce(identity, accumulator, (a, b) -> { throw new AssertionError(); });
    }
    @Override
    @SuppressWarnings("unchecked")
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> __) {
      return (U)reducer.loop(identity, () -> true, (acc, element) -> accumulator.apply((U)acc, element));
    }
    @Override
    @SuppressWarnings("unchecked")
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
      Object none = new Object();
      Object result = reduce(none, (a, b) -> {
          if (a == none) {
            return b;
          }
          return accumulator.apply((T)a, b);
        }, (a, b) -> { throw new AssertionError(); });
      return (result == none)? Optional.empty(): Optional.of((T)result);
    }
    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> __) {
      R initial = supplier.get();
      reduce(initial, (a, b) -> { accumulator.accept(a, b); return null; }, (a, b) -> { throw new AssertionError(); });
      return initial;
    }
    
    @Override
    public Object[] toArray() {
      return collect(Collectors.toList()).toArray();
    }
    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
      List<T> list = collect(Collectors.toList());
      return list.toArray(generator.apply(list.size()));
    }
    
    @Override
    public <R, A> R collect(Collector<T, A, R> collector) {
      return collector.doCollect(this);
    }
    @Override
    public Iterator<T> iterator() {
      return collect(Collectors.toList()).iterator();
    }
    
    
    @Override
    public Stream<T> distinct() {
      HashSet<T> set = new HashSet<>();
      return new StreamImpl<>(closer, (initial, test, fun) ->
        reducer.loop(initial, test, (acc, element) -> {
          if(set.add(element)) {
            return fun.apply(acc, element);
          }
          return acc;
        }));
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Stream<T> sorted() {
      return sorted((e1, e2) -> ((Comparable)e1).compareTo(e2));
    }
    
    @Override
    public Stream<T> sorted(Comparator<? super T> comparator) {
      return new StreamImpl<>(closer, (initial, test, fun) -> {
        List<T> list = collect(Collectors.toList());
        Collections.sort(list, comparator);
        return reduceLoop(list.iterator(), initial, test, fun);
      });
    }
    
    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
      return reduce((a, b) -> comparator.compare(a, b) < 0? b: a);
    }
    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
      return reduce((a, b) -> comparator.compare(a, b) < 0? a: b);
    }
    @Override
    public long count() {
      return mapToLong(e -> 1).reduce(0, (a, b) -> a + b);
    }
    
    
    @Override
    public void close() {
      closer.run();
    }
    @Override
    public Stream<T> onClose(Runnable closeHandler) {
      return new StreamImpl<>(() -> {
        closer.run();
        closeHandler.run();
      }, reducer);
    }

    @Override
    public boolean isParallel() {
      return false;
    }
    @Override
    public Stream<T> sequential() {
      return this;
    }
    @Override
    public Stream<T> parallel() {
      return this;
    }
    @Override
    public Stream<T> unordered() {
      return this;
    }
  }
  
  static class Average { long sum; long count; }
  
  static final class IntStreamImpl implements IntStream {
    private final Stream<Integer> impl;
    
    IntStreamImpl(Stream<Integer> impl) {
      this.impl = impl;
    }
    
    @Override
    public IntStream filter(IntPredicate predicate) {
      return new IntStreamImpl(impl.filter(predicate::test));
    }
    
    @Override
    public IntStream map(IntUnaryOperator mapper) {
      return new IntStreamImpl(impl.map(mapper::applyAsInt));
    }
    @Override
    public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
      return impl.map(mapper::apply);
    }
    @Override
    public LongStream mapToLong(IntToLongFunction mapper) {
      return impl.mapToLong(mapper::applyAsLong);
    }
    @Override
    public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
      return impl.mapToDouble(mapper::applyAsDouble);
    }

    @Override
    public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
      return impl.flatMapToInt(mapper::apply);
    }

    @Override
    public IntStream distinct() {
      return new IntStreamImpl(impl.distinct());
    }
    @Override
    public IntStream sorted() {
      return new IntStreamImpl(impl.sorted());
    }

    @Override
    public IntStream peek(IntConsumer action) {
      return new IntStreamImpl(impl.peek(action::accept));
    }

    @Override
    public IntStream limit(long maxSize) {
      return new IntStreamImpl(impl.limit(maxSize));
    }
    @Override
    public IntStream skip(long n) {
      return new IntStreamImpl(impl.skip(n));
    }

    @Override
    public void forEach(IntConsumer action) {
      impl.forEach(action::accept);
    }
    @Override
    public void forEachOrdered(IntConsumer action) {
      forEach(action);
    }

    @Override
    public int[] toArray() {
      List<Integer> list = impl.collect(Collectors.toList());
      int[] array = new int[list.size()];
      for(int i = 0; i < array.length; i++) {
        array[i] = list.get(i);
      }
      return array;
    }
    
    private static OptionalInt unbox(Optional<Integer> optional) {
      return optional.map(OptionalInt::of).orElse(OptionalInt.empty());
    }
    
    @Override
    public int reduce(int identity, IntBinaryOperator op) {
      return impl.reduce(identity, op::applyAsInt);
    }
    @Override
    public OptionalInt reduce(IntBinaryOperator op) {
      return unbox(impl.reduce(op::applyAsInt));
    }
    @Override
    public <R> R collect(Supplier<R> supplier,
                  ObjIntConsumer<R> accumulator,
                  BiConsumer<R, R> combiner) {
      return impl.collect(supplier::get, accumulator::accept, combiner::accept);
    }
    
    @Override
    public int sum() {
      return reduce(0, (a, b) -> a + b);
    }
    @Override
    public OptionalInt min() {
      return reduce(Math::min);
    }
    @Override
    public OptionalInt max() {
      return reduce(Math::max);
    }
    @Override
    public long count() {
      return impl.count();
    }

    @Override
    public OptionalDouble average() {
      Average average = impl.collect(Average::new, (avg, value) -> { avg.sum += value; avg.sum++; }, (a, b) -> { throw new AssertionError(); });
      return (average.count == 0)? OptionalDouble.empty(): OptionalDouble.of(((double)average.sum) / average.count);
    }

    /*IntSummaryStatistics summaryStatistics();*/

    @Override
    public boolean anyMatch(IntPredicate predicate) {
      return impl.anyMatch(predicate::test);
    }
    @Override
    public boolean allMatch(IntPredicate predicate) {
      return impl.allMatch(predicate::test);
    }
    @Override
    public boolean noneMatch(IntPredicate predicate) {
      return impl.anyMatch(predicate::test);
    }

    @Override
    public OptionalInt findFirst() {
      return unbox(impl.findFirst());
    }
    @Override
    public OptionalInt findAny() {
      return findFirst();
    }

    @Override
    public LongStream asLongStream() {
      return mapToLong(e -> (long)e);
    }
    @Override
    public DoubleStream asDoubleStream() {
      return mapToDouble(e -> (double)e);
    }
    @Override
    public Stream<Integer> boxed() {
      return impl;
    }
    @Override
    public Iterator<Integer> iterator() {
      return impl.iterator();
    }
    
    @Override
    public void close() {
      impl.close();
    }
    @Override
    public IntStream onClose(Runnable closeHandler) {
      return new IntStreamImpl(impl.onClose(closeHandler));
    }

    @Override
    public boolean isParallel() {
      return false;
    }
    @Override
    public IntStream sequential() {
      return this;
    }
    @Override
    public IntStream parallel() {
      return this;
    }
    @Override
    public IntStream unordered() {
      return this;
    }
  }
  
  static final class LongStreamImpl implements LongStream {
    private final Stream<Long> impl;
    
    LongStreamImpl(Stream<Long> impl) {
      this.impl = impl;
    }
    
    @Override
    public LongStream filter(LongPredicate predicate) {
      return new LongStreamImpl(impl.filter(predicate::test));
    }
    
    @Override
    public LongStream map(LongUnaryOperator mapper) {
      return impl.mapToLong(mapper::applyAsLong);
    }
    @Override
    public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
      return impl.map(mapper::apply);
    }
    @Override
    public IntStream mapToInt(LongToIntFunction mapper) {
      return impl.mapToInt(mapper::applyAsInt);
    }
    @Override
    public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
      return impl.mapToDouble(mapper::applyAsDouble);
    }
    

    @Override
    public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
      return impl.flatMapToLong(mapper::apply);
    }

    @Override
    public LongStream distinct() {
      return new LongStreamImpl(impl.distinct());
    }
    @Override
    public LongStream sorted() {
      return new LongStreamImpl(impl.sorted());
    }

    @Override
    public LongStream peek(LongConsumer action) {
      return new LongStreamImpl(impl.peek(action::accept));
    }

    @Override
    public LongStream limit(long maxSize) {
      return new LongStreamImpl(impl.limit(maxSize));
    }
    @Override
    public LongStream skip(long n) {
      return new LongStreamImpl(impl.skip(n));
    }

    @Override
    public void forEach(LongConsumer action) {
      impl.forEach(action::accept);
    }
    @Override
    public void forEachOrdered(LongConsumer action) {
      forEach(action);
    }

    @Override
    public long[] toArray() {
      List<Long> list = impl.collect(Collectors.toList());
      long[] array = new long[list.size()];
      for(int i = 0; i < array.length; i++) {
        array[i] = list.get(i);
      }
      return array;
    }
    
    private static OptionalLong unbox(Optional<Long> optional) {
      return optional.map(OptionalLong::of).orElse(OptionalLong.empty());
    }
    
    @Override
    public long reduce(long identity, LongBinaryOperator op) {
      return impl.reduce(identity, op::applyAsLong);
    }
    @Override
    public OptionalLong reduce(LongBinaryOperator op) {
      return unbox(impl.reduce(op::applyAsLong));
    }
    @Override
    public <R> R collect(Supplier<R> supplier,
                  ObjLongConsumer<R> accumulator,
                  BiConsumer<R, R> combiner) {
      return impl.collect(supplier::get, accumulator::accept, combiner::accept);
    }
    
    @Override
    public long sum() {
      return reduce(0, (a, b) -> a + b);
    }
    @Override
    public OptionalLong min() {
      return reduce(Math::min);
    }
    @Override
    public OptionalLong max() {
      return reduce(Math::max);
    }
    @Override
    public long count() {
      return impl.count();
    }

    @Override
    public OptionalDouble average() {
      Average average = impl.collect(Average::new, (avg, value) -> { avg.sum += value; avg.sum++; }, (a, b) -> { throw new AssertionError(); });
      return (average.count == 0)? OptionalDouble.empty(): OptionalDouble.of(((double)average.sum) / average.count);
    }

    /*IntSummaryStatistics summaryStatistics();*/

    @Override
    public boolean anyMatch(LongPredicate predicate) {
      return impl.anyMatch(predicate::test);
    }
    @Override
    public boolean allMatch(LongPredicate predicate) {
      return impl.allMatch(predicate::test);
    }
    @Override
    public boolean noneMatch(LongPredicate predicate) {
      return impl.anyMatch(predicate::test);
    }

    @Override
    public OptionalLong findFirst() {
      return unbox(impl.findFirst());
    }
    @Override
    public OptionalLong findAny() {
      return findFirst();
    }

    @Override
    public DoubleStream asDoubleStream() {
      return mapToDouble(e -> (double)e);
    }
    
    @Override
    public Stream<Long> boxed() {
      return impl;
    }
    @Override
    public Iterator<Long> iterator() {
      return impl.iterator();
    }
    
    @Override
    public void close() {
      impl.close();
    }
    @Override
    public LongStream onClose(Runnable closeHandler) {
      return new LongStreamImpl(impl.onClose(closeHandler));
    }

    @Override
    public boolean isParallel() {
      return false;
    }
    @Override
    public LongStream sequential() {
      return this;
    }
    @Override
    public LongStream parallel() {
      return this;
    }
    @Override
    public LongStream unordered() {
      return this;
    }
  }
  
  static final class DoubleStreamImpl implements DoubleStream {
    private final Stream<Double> impl;
    
    DoubleStreamImpl(Stream<Double> impl) {
      this.impl = impl;
    }
    
    @Override
    public DoubleStream filter(DoublePredicate predicate) {
      return new DoubleStreamImpl(impl.filter(predicate::test));
    }
    
    @Override
    public DoubleStream map(DoubleUnaryOperator mapper) {
      return impl.mapToDouble(mapper::applyAsDouble);
    }
    @Override
    public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
      return impl.map(mapper::apply);
    }
    @Override
    public IntStream mapToInt(DoubleToIntFunction mapper) {
      return impl.mapToInt(mapper::applyAsInt);
    }
    @Override
    public LongStream mapToLong(DoubleToLongFunction mapper) {
      return impl.mapToLong(mapper::applyAsLong);
    }
    

    @Override
    public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
      return impl.flatMapToDouble(mapper::apply);
    }

    @Override
    public DoubleStream distinct() {
      return new DoubleStreamImpl(impl.distinct());
    }
    @Override
    public DoubleStream sorted() {
      return new DoubleStreamImpl(impl.sorted());
    }

    @Override
    public DoubleStream peek(DoubleConsumer action) {
      return new DoubleStreamImpl(impl.peek(action::accept));
    }

    @Override
    public DoubleStream limit(long maxSize) {
      return new DoubleStreamImpl(impl.limit(maxSize));
    }
    @Override
    public DoubleStream skip(long n) {
      return new DoubleStreamImpl(impl.skip(n));
    }

    @Override
    public void forEach(DoubleConsumer action) {
      impl.forEach(action::accept);
    }
    @Override
    public void forEachOrdered(DoubleConsumer action) {
      forEach(action);
    }

    @Override
    public double[] toArray() {
      List<Double> list = impl.collect(Collectors.toList());
      double[] array = new double[list.size()];
      for(int i = 0; i < array.length; i++) {
        array[i] = list.get(i);
      }
      return array;
    }
    
    private static OptionalDouble unbox(Optional<Double> optional) {
      return optional.map(OptionalDouble::of).orElse(OptionalDouble.empty());
    }
    
    @Override
    public double reduce(double identity, DoubleBinaryOperator op) {
      return impl.reduce(identity, op::applyAsDouble);
    }
    @Override
    public OptionalDouble reduce(DoubleBinaryOperator op) {
      return unbox(impl.reduce(op::applyAsDouble));
    }
    @Override
    public <R> R collect(Supplier<R> supplier,
                  ObjDoubleConsumer<R> accumulator,
                  BiConsumer<R, R> combiner) {
      return impl.collect(supplier::get, accumulator::accept, combiner::accept);
    }
    
    @Override
    public double sum() {
      return reduce(0, (a, b) -> a + b);
    }
    @Override
    public OptionalDouble min() {
      return reduce(Math::min);
    }
    @Override
    public OptionalDouble max() {
      return reduce(Math::max);
    }
    @Override
    public long count() {
      return impl.count();
    }

    @Override
    public OptionalDouble average() {
      Average average = impl.collect(Average::new, (avg, value) -> { avg.sum += value; avg.sum++; }, (a, b) -> { throw new AssertionError(); });
      return (average.count == 0)? OptionalDouble.empty(): OptionalDouble.of(((double)average.sum) / average.count);
    }

    /*IntSummaryStatistics summaryStatistics();*/

    @Override
    public boolean anyMatch(DoublePredicate predicate) {
      return impl.anyMatch(predicate::test);
    }
    @Override
    public boolean allMatch(DoublePredicate predicate) {
      return impl.allMatch(predicate::test);
    }
    @Override
    public boolean noneMatch(DoublePredicate predicate) {
      return impl.anyMatch(predicate::test);
    }

    @Override
    public OptionalDouble findFirst() {
      return unbox(impl.findFirst());
    }
    @Override
    public OptionalDouble findAny() {
      return findFirst();
    }

    @Override
    public Stream<Double> boxed() {
      return impl;
    }
    @Override
    public Iterator<Double> iterator() {
      return impl.iterator();
    }
    
    @Override
    public void close() {
      impl.close();
    }
    @Override
    public DoubleStream onClose(Runnable closeHandler) {
      return new DoubleStreamImpl(impl.onClose(closeHandler));
    }

    @Override
    public boolean isParallel() {
      return false;
    }
    @Override
    public DoubleStream sequential() {
      return this;
    }
    @Override
    public DoubleStream parallel() {
      return this;
    }
    @Override
    public DoubleStream unordered() {
      return this;
    }
  }
}
