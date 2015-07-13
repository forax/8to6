package com.github.forax._8to6.rt.java.util.stream;

import com.github.forax._8to6.rt.java.util.OptionalDouble;
import com.github.forax._8to6.rt.java.util.function.BiConsumer;
import com.github.forax._8to6.rt.java.util.function.DoubleBinaryOperator;
import com.github.forax._8to6.rt.java.util.function.DoubleConsumer;
import com.github.forax._8to6.rt.java.util.function.DoubleFunction;
import com.github.forax._8to6.rt.java.util.function.DoublePredicate;
import com.github.forax._8to6.rt.java.util.function.DoubleToIntFunction;
import com.github.forax._8to6.rt.java.util.function.DoubleToLongFunction;
import com.github.forax._8to6.rt.java.util.function.DoubleUnaryOperator;
import com.github.forax._8to6.rt.java.util.function.ObjDoubleConsumer;
import com.github.forax._8to6.rt.java.util.function.Supplier;

public interface DoubleStream {

    
    DoubleStream filter(DoublePredicate predicate);

    
    DoubleStream map(DoubleUnaryOperator mapper);
    <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper);
    IntStream mapToInt(DoubleToIntFunction mapper);
    LongStream mapToLong(DoubleToLongFunction mapper);

    DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper);

    
    DoubleStream distinct();
    DoubleStream sorted();

    DoubleStream peek(DoubleConsumer action);
    
    DoubleStream limit(long maxSize);
    DoubleStream skip(long n);

    
    void forEach(DoubleConsumer action);
    void forEachOrdered(DoubleConsumer action);

    double[] toArray();
    double reduce(double identity, DoubleBinaryOperator op);

    OptionalDouble reduce(DoubleBinaryOperator op);

    <R> R collect(Supplier<R> supplier,
                  ObjDoubleConsumer<R> accumulator,
                  BiConsumer<R, R> combiner);
    
    double sum();
    
    OptionalDouble min();    
    OptionalDouble max();

    long count();
   
    OptionalDouble average();
    
    /*DoubleSummaryStatistics summaryStatistics();*/

    
    boolean anyMatch(DoublePredicate predicate);    
    boolean allMatch(DoublePredicate predicate);
    boolean noneMatch(DoublePredicate predicate);

    OptionalDouble findFirst();
    OptionalDouble findAny();

    Stream<Double> boxed();    
}
