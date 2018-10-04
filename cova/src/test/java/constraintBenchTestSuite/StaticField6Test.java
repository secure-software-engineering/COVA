package constraintBenchTestSuite;

import org.junit.Assert;
import org.junit.Ignore;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class StaticField6Test extends ConstraintBenchTestFramework {

  public StaticField6Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField6";
  }

  @Ignore
  public void test() {
    Assert.assertTrue(results.containsKey(21));
  }
}
