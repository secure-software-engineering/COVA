package constraintBenchTestSuite;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class StaticField7Test extends ConstraintBenchTestFramework {

  public StaticField7Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField7";
  }

  @Test
  public void test() {
    Assert.assertTrue(results.containsKey(21));
  }
}
