package constraintBenchTestSuite.stringOperations;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String2Test extends ConstraintBenchTestFramework {
  public String2Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String2";
  }

  @Test
  public void test() {}
}
