package jp.jyane.grpc.annotations.checkers;

import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.IdentifierTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.IdentifierTree;

@BugPattern(
    name = "InternalApi",
    summary = "@InternalApi should not be used in application code",
    explanation = "@InternalApi should not be used in application code",
    severity = SeverityLevel.WARNING
)
public final class InternalChecker extends BugChecker implements IdentifierTreeMatcher {
  private static final String ANNOTATION = "io.grpc.Internal";

  @Override
  public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
    return describeMatch(identifierTree);
  }
}
