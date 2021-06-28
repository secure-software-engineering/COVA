package constraintBenchTestSuite;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String1Test extends ConstraintBenchTestFramework {
  public String1Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String1";
  }

  @Test
  public void test() {}
}
