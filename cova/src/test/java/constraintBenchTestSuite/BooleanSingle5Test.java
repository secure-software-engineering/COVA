package constraintBenchTestSuite;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 * 
 */
public class BooleanSingle5Test extends ConstraintBenchTestFramework {

  public BooleanSingle5Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanSingle5";
  }

  @Test
  public void test() {
    Assert.assertEquals(0, results.size());
  }
}
