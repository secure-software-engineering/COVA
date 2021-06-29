package constraintBenchTestSuite.stringOperations;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String7Test extends ConstraintBenchTestFramework {
  public String7Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String7";
  }

  @Test
  public void test() {}
}
