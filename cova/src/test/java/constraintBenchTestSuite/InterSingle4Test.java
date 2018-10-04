package constraintBenchTestSuite;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class InterSingle4Test extends ConstraintBenchTestFramework {

  public InterSingle4Test() {
    targetTestClassName = "constraintBench.test.interProcedural.InterSingle4";
  }

  @Test
  public void test() {
    Assert.assertEquals(0, results.size());
  }
}
