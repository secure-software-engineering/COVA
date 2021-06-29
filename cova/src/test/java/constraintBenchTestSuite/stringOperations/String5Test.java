package constraintBenchTestSuite.stringOperations;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String5Test extends ConstraintBenchTestFramework {
  public String5Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String5";
  }

  @Test
  public void test() {}
}
