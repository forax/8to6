package com.fithub.forax._8to6;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class CompatInfoCreator {
  public static void main(String[] args) throws IOException {
    Path path = Paths.get("/usr/jdk/jdk1.7.0_71/jre/lib/rt.jar");
    TreeSet<String> methodSigSet = new TreeSet<>();
    try(JarFile jarFile = new JarFile(path.toFile());
        Stream<JarEntry> stream = jarFile.stream()) {
      stream.filter(entry -> entry.getName().endsWith(".class"))
            .forEach(entry -> {
              try(InputStream input = jarFile.getInputStream(entry)) {
                ClassReader reader = new ClassReader(input);
                if ((reader.getAccess() & ACC_PUBLIC) != 0) {
                  reader.accept(new ClassVisitor(ASM5) {
                    private String className;

                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                      this.className = name;
                    }
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                      if ((access & (ACC_PUBLIC|ACC_PROTECTED)) == 0) {
                        return null;
                      }
                      methodSigSet.add(className + '.' + name + desc);
                      return null;
                    }
                  }, ClassReader.SKIP_CODE);
                }
              } catch(IOException e) {
                throw new IOError(e);
              }
            });
    }
    
    Files.write(Paths.get("compat-1.7-methodsig.txt"), methodSigSet);
    System.out.println("found " + methodSigSet.size() + " signatures");
  }
}
