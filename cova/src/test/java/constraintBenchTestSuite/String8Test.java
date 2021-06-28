package constraintBenchTestSuite;

import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String8Test extends ConstraintBenchTestFramework {
  public String8Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String8";
  }

  @Test
  public void test() {}
}
