package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle6 {

  public void test() {
    boolean b = false;
    if (Configuration.featureA()) {
      b = true; // A
    }
    if (b) {
      while (callee(0) < 2) {// A
        System.out.println();// A
      }
    }
  }

  private int callee(int i) {
    return i + 1;// A
  }

}
