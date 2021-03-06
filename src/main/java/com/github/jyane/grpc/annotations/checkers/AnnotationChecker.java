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

package com.github.jyane.grpc.annotations.checkers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.errorprone.matchers.Description.NO_MATCH;

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
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;

abstract class AnnotationChecker extends BugChecker implements IdentifierTreeMatcher,
    MemberSelectTreeMatcher, CompilationUnitTreeMatcher {

  private boolean shouldBeChecked = false;
  private final String annotationType;
  private final String basePackage;

  AnnotationChecker(String basePackage, String annotationType) {
    this.annotationType = checkNotNull(annotationType, "annotationType");
    this.basePackage = checkNotNull(basePackage, "packageName");
  }

  /**
   * Returns true if api is annotated.
   */
  private Optional<AnnotationMirror> findAnnotatedApi(Symbol symbol) {
    if (symbol == null) {
      return Optional.empty();
    }
    for (AnnotationMirror annotation : symbol.getAnnotationMirrors()) {
      if (annotation.getAnnotationType().toString().equals(annotationType)) {
        return Optional.of(annotation);
      }
    }
    // recursive
    return findAnnotatedApi(symbol.owner);
  }

  /**
   * Returns the description if tree is annotated.
   */
  private Description match(Tree tree, VisitorState state) {
    Symbol symbol = ASTHelpers.getSymbol(tree);
    if (!shouldBeChecked || symbol == null) {
      return NO_MATCH;
    }
    return findAnnotatedApi(symbol)
        .map(x -> describe(tree, x))
        .orElse(NO_MATCH);
  }

  protected abstract Description describe(Tree tree, AnnotationMirror annotation);

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
    // package is under the basePackage.
    if (!(name.equals(basePackage) || name.startsWith(basePackage + "."))) {
      shouldBeChecked = true;
    }
    return NO_MATCH;
  }
}
