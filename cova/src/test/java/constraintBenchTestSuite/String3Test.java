package constraintBenchTestSuite;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String3Test extends ConstraintBenchTestFramework {
  public String3Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String3";
  }

  @Test
  public void test() {}
}
