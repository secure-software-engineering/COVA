package constraintBenchTestSuite;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String4Test extends ConstraintBenchTestFramework {
  public String4Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String4";
  }

  @Test
  public void test() {}
}
