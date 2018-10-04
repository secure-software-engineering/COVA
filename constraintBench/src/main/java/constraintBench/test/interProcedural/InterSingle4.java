package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle4 {

  public void test() {
    callee(Configuration.featureA());
    callee(true);
  }

  private void callee(boolean a) {
    if (a) {
      boolean b = a;
      System.out.println();
      if (b) {
        System.out.println();
      }
    }
  }

}
