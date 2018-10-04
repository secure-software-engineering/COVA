package constraintBenchTestSuite;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class InstanceField6Test extends ConstraintBenchTestFramework {

  public InstanceField6Test() {
    targetTestClassName = "constraintBench.test.instanceField.InstanceField6";
  }

  @Test
  public void test() {
    Assert.assertEquals(0, results.size());
  }
}
