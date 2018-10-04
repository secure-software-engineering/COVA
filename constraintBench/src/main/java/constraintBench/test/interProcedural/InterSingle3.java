package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle3 {

  public void test() {
    callee(Configuration.featureA());
  }

  private void callee(boolean a) {
    if (a) {
      System.out.println();// A
    }
  }

}
