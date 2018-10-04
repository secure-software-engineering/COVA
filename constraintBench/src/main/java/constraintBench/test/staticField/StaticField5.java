package constraintBench.test.staticField;

import constraintBench.utils.Configuration;
import constraintBench.utils.StaticField;

/**
 * 
 *
 */
public class StaticField5 {

  public void test()
  {
    StaticField.a = Configuration.featureA();
    callee1();
    callee2();
  }

  private void callee1() {
    StaticField.a = false;// strong update
  }

  private void callee2() {
    if (StaticField.a) {
      System.out.println();// no constraint
    }
  }

}
