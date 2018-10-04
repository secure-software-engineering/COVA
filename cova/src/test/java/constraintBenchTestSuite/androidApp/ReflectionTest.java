package constraintBenchTestSuite.androidApp;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFrameworkForAndroidApp;

public class ReflectionTest extends ConstraintBenchTestFrameworkForAndroidApp {

  public ReflectionTest() {
    targetTestAppName = "ReflectionTest";
  }

  @Test
  public void test() {
    Assert.assertTrue(results.size() > 0);
  }
}
