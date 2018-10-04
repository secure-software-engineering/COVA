package constraintBenchTestSuite;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class StaticField5Test extends ConstraintBenchTestFramework {

  public StaticField5Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField5";
  }

  @Test
  public void test() {
    Assert.assertTrue(results.size() == 0);
  }
}
