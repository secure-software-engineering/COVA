package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle5 {

  public void test() {
    if (Configuration.featureA()) {
      callee(); // A
    }
    callee();
  }

  private void callee() {
    System.out.println();
  }

}
