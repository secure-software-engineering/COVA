package constraintBench.test.staticField;

import constraintBench.utils.StaticField;

/**
 * 
 * 
 */
public class StaticField2 {

  public void test()
  {
    StaticField.setFeature();
    callee();
  }

  private void callee()
  {
    boolean a = StaticField.f;
    if (a) {
      System.out.println();// (A ^ B) v !B)
    }
  }

}
