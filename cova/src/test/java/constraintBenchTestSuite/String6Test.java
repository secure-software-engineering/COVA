package constraintBenchTestSuite;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String6Test extends ConstraintBenchTestFramework {
  public String6Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String6";
  }

  @Test
  public void test() {}
}
