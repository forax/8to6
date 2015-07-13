package com.github.forax._8to6.rt.java.util.stream;

import java.io.Closeable;
import java.util.Iterator;

interface BaseStream<T, S extends BaseStream<T, S>> extends Closeable {
  Iterator<T> iterator();

  /*Spliterator<T> spliterator();*/

  boolean isParallel();

  S sequential();

  S parallel();

  S unordered();

  S onClose(Runnable closeHandler);

  @Override
  void close();
}
