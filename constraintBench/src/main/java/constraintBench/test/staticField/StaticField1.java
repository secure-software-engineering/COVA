package constraintBench.test.staticField;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class StaticField1 {

  private static class TestClass {
    public static boolean f;
  }

  public void test() {
    TestClass.f = Configuration.featureA();
    if (TestClass.f) {
      System.out.println();// A
    }
    callee();
    if (TestClass.f) {
      System.out.println();// B
    }
  }

  private void callee() {
    TestClass.f = Configuration.featureB();
  }

}
