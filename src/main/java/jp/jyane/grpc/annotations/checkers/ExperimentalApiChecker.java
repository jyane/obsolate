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

import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.IdentifierTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.IdentifierTree;

@BugPattern(
    name = "ExperimentalApi",
    summary = "@ExperimentalApi should not be used in application code",
    explanation = "@ExperimentalApi should not be used in application code",
    severity = SeverityLevel.WARNING
)
public class ExperimentalApiChecker extends BugChecker implements IdentifierTreeMatcher {

  @Override
  public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
    return null;
  }
}
