package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle2 {

  public void test() {
    boolean a = Configuration.featureA();
    boolean b1 = callee(a);
    if (b1) {
      System.out.println();// A
    }
  }

  private boolean callee(boolean a) {
    int b = a ? 1 : 0;
    if (b == 0) {
      System.out.println();// !A
    }
    return a;
  }

}
