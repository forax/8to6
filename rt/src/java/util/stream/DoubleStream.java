package java.util.stream;

import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

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
