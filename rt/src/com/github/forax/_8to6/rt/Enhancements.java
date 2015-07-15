package com.github.forax._8to6.rt;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.github.forax._8to6.rt.java.util.stream.Collector;
import com.github.forax._8to6.rt.java.util.stream.Collectors;
import com.github.forax._8to6.rt.java.util.stream.DoubleStream;
import com.github.forax._8to6.rt.java.util.stream.IntStream;
import com.github.forax._8to6.rt.java.util.stream.LongStream;
import com.github.forax._8to6.rt.java.util.stream.Stream;


public class Enhancements {
  // --- lambda metafactory
  
  private static final MethodHandle PROXY;
  private static final MethodHandle INSERT_ARGUMENTS;
  static {
    Lookup lookup = MethodHandles.lookup();
    try {
      PROXY = lookup.findStatic(Enhancements.class, "proxy",
          MethodType.methodType(Object.class, Class.class, MethodHandle.class));
      INSERT_ARGUMENTS =  lookup.findStatic(Enhancements.class, "insertArguments",
                  MethodType.methodType(MethodHandle.class, MethodHandle.class, Object[].class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }
  
  // can not create a method handle on a method of java.lang.invoke, need a trampoline
  private static Object proxy(Class<?> interfaze, MethodHandle target) {
    return MethodHandleProxies.asInterfaceInstance(interfaze, target);
  }
  @SuppressWarnings("unused")
  private static MethodHandle insertArguments(MethodHandle target, Object... values) {
    return MethodHandles.insertArguments(target, 0, values);
  }
  
  public static CallSite metafactory(Lookup lookup, String name, MethodType type,
      MethodType sig, MethodHandle impl, MethodType reifiedSig) throws Throwable {

    Class<?> interfaze = type.returnType();
    if (type.parameterCount() == 0) {   // constant lambda
      MethodHandle target = impl.asType(reifiedSig);
      Object proxy = proxy(interfaze, target);
      return new ConstantCallSite(MethodHandles.constant(interfaze, proxy));
    }
    
    MethodHandle binder = INSERT_ARGUMENTS
        .bindTo(impl)
        .asCollector(Object[].class, type.parameterCount())
        .asType(type.changeReturnType(MethodHandle.class));
    
    MethodHandle proxyFactory = MethodHandles
        .dropArguments(PROXY.bindTo(interfaze), 1, type.parameterList());
    
    MethodHandle target = MethodHandles
        .foldArguments(proxyFactory, binder)
        .asType(type);
    
    return new ConstantCallSite(target);
  }
}
