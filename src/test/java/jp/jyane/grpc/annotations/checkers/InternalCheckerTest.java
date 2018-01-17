package jp.jyane.grpc.annotations.checkers;

import com.google.errorprone.CompilationTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InternalCheckerTest {
  private CompilationTestHelper compiler;

  @Before
  public void setUp() {
    compiler = CompilationTestHelper.newInstance(InternalChecker.class, getClass());

    // add the @Internal annotation
    compiler.addSourceLines(
        "io/grpc/Internal.java",
        "package io.grpc;",
        "import java.lang.annotation.Documented;",
        "import java.lang.annotation.ElementType;",
        "import java.lang.annotation.Retention;",
        "import java.lang.annotation.RetentionPolicy;",
        "import java.lang.annotation.Target;",
        "",
        "@Internal",
        "@Retention(RetentionPolicy.SOURCE)",
        "@Target({",
        "  ElementType.ANNOTATION_TYPE,",
        "  ElementType.CONSTRUCTOR,",
        "  ElementType.FIELD,",
        "  ElementType.METHOD,",
        "  ElementType.PACKAGE,",
        "  ElementType.TYPE})",
        "@Documented",
        "public @interface Internal {}");

    // add an annotated class
    compiler.addSourceLines(
        "io/grpc/AnnotatedClass.java",
        "package io.grpc;",
        "import io.grpc.Internal;",
        "",
        "@Internal",
        "public class AnnotatedClass {",
        "  public AnnotatedClass() {}",
        "}");
  }

  @Test
  public void negative() {
    compiler
        .addSourceLines(
            "example/Test.java",
            "package example;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    System.out.println(args);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveInstantiation() {
    compiler
        .addSourceLines(
            "example/Test.java",
            "package example;",
            "",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    new AnnotatedClass();",
            "  }",
            "}"
        ).doTest();
  }
}
