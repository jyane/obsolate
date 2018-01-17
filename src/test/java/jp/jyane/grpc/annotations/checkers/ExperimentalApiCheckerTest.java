/*
 * Copyright 2018 jyane.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.jyane.grpc.annotations.checkers;

import com.google.errorprone.CompilationTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExperimentalApiCheckerTest {
  private CompilationTestHelper compiler;

  @Before
  public void setUp() {
    compiler = CompilationTestHelper.newInstance(InternalChecker.class, getClass());
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
}
