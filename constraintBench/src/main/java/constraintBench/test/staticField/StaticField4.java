package constraintBench.test.staticField;

import constraintBench.utils.Configuration;
import constraintBench.utils.StaticField;

/**
 * 
 *
 */
public class StaticField4 {

  public void test()
  {
    callee1();
    StaticField.a = Configuration.featureA();
    callee2();
  }

  private void callee1() {
    StaticField.b = Configuration.featureB();
  }

  private void callee2() {
    if (StaticField.a && StaticField.b) {
      System.out.println();
    }
  }

}
