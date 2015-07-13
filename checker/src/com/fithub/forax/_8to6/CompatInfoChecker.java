package com.fithub.forax._8to6;

import static org.objectweb.asm.Opcodes.ASM5;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class CompatInfoChecker {
  private static void scan(Stream<InputStream> stream, Map<String, Set<String>> signatureMap) throws IOException {
    try {
      stream
        .forEach(input -> {
          try {
            ClassReader reader = new ClassReader(input);
            input.close();
            reader.accept(new ClassVisitor(ASM5) {
              String className;

              @Override
              public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                this.className = name;
              }
              @Override
              public MethodVisitor visitMethod(int access, String mname, String mdesc, String signature, String[] exceptions) {
                return new MethodVisitor(ASM5) {
                  @Override
                  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    Set<String> methods = signatureMap.get(owner);
                    if (methods == null) {  // unknown class
                      if (owner.startsWith("java.") || owner.startsWith("javax.")) {
                        System.err.println("unknown class " + owner);
                      }
                      return;
                    }
                    if (methods.contains(name + desc)) {  // ok, method found !
                      return;
                    }
                    System.err.println("unknown method " + owner + '.' + name + desc + " in " + className +'.' + mname + mdesc);
                  }
                };
              }
            }, 0);
          } catch(IOException e) {
            throw new IOError(e);
          }
        });
    } catch(IOError e) {
      throw (IOException)e.getCause();
    }
  }
  
  interface IOFunction<T, R> {
    public R apply(T t) throws IOException;
  }
  
  private static <T, R extends Closeable> Function<T, R> io(IOFunction<? super T, ? extends R> fun) {
    return element -> {
      try {
        R res = fun.apply(element);
        return res;
      } catch(IOException e) {
        throw new IOError(e);
      }
    };
  }
  
  public static void main(String[] args) throws IOException {
    Path compatPath = Paths.get("compat-1.7-methodsig.txt");
    Map<String, Set<String>> signatureMap;
    try(Stream<String> lines = Files.lines(compatPath)) {
      signatureMap =
          lines.map(line -> line.split("\\."))
               .collect(Collectors.groupingBy(tokens -> tokens[0],
                           Collectors.mapping(tokens -> tokens[1],
                               Collectors.toSet())));
    }
    System.out.println("load " + signatureMap.size() + " classes");
    
    Path inputPath = Paths.get(args[0]);
    if (inputPath.toString().endsWith(".jar")) {
      try(JarFile jarFile = new JarFile(inputPath.toFile());
          Stream<JarEntry> stream = jarFile.stream()) {
        scan(stream.filter(entry -> entry.getName().endsWith(".class"))
                   .map(io(jarFile::getInputStream)),
             signatureMap);
      }
    } else {
      try(Stream<Path> stream = Files.walk(inputPath)) {
        scan(stream.filter(path -> path.toString().endsWith(".class"))
                   .map(io(Files::newInputStream)),
             signatureMap);
      }
    }
   
    System.out.println("scan completed !");
  }

  
}
