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

import static com.google.errorprone.matchers.Description.NO_MATCH;

import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.CompilationUnitTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.IdentifierTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.MemberSelectTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import javax.lang.model.element.AnnotationMirror;

@BugPattern(
    name = "Internal",
    summary = "@Internal should not be used in application code",
    explanation = "@Internal should not be used in application code",
    severity = SeverityLevel.WARNING
)
public final class InternalChecker extends BugChecker implements
    CompilationUnitTreeMatcher, IdentifierTreeMatcher, MemberSelectTreeMatcher {

  private boolean shouldBeChecked = false;

  /**
   * Returns true if api is annotated.
   */
  private boolean isAnnotatedApi(Symbol symbol) {
    if (symbol == null) {
      return false;
    }
    for (AnnotationMirror annotation : symbol.getAnnotationMirrors()) {
      if (annotation.getAnnotationType().toString().equals("io.grpc.Internal")) {
        return true;
      }
    }
    // recursive
    return isAnnotatedApi(symbol.owner);
  }

  /**
   * Returns the description if tree is annotated.
   */
  private Description match(Tree tree, VisitorState state) {
    Symbol symbol = ASTHelpers.getSymbol(tree);
    if (!shouldBeChecked || symbol == null) {
      return NO_MATCH;
    }
    if (!isAnnotatedApi(symbol)) {
      return NO_MATCH;
    }
    return describeMatch(tree);
  }

  @Override
  public Description matchIdentifier(IdentifierTree tree, VisitorState state) {
    return match(tree, state);
  }

  @Override
  public Description matchMemberSelect(MemberSelectTree tree, VisitorState state) {
    return match(tree, state);
  }

  // TODO(jyane): verify order.
  // Compilation unit tree would be matched first and then other trees would be matched. Really?
  @Override
  public Description matchCompilationUnit(CompilationUnitTree tree, VisitorState state) {
    Tree packageName = tree.getPackageName();
    if (packageName == null) {
      return NO_MATCH;
    }
    String name = packageName.toString();
    // The package name is not starting with `io.grpc` or not `io.grpc`.
    if (!(name.equals("io.grpc") || name.startsWith("io.grpc."))) {
      shouldBeChecked = true;
    }
    return NO_MATCH;
  }
}
