package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterMultiple1 {

  public void test() {
    boolean a = !callee();
    int flag = 0;
    if (a && Configuration.featureB()) {
      flag = 3; // (A ^ B)
    } else {
      flag = 4; // (!A v !B)
    }
    System.out.println(flag);
  }

  private boolean callee() {
    if (Configuration.featureA()) {
      return false; // A
    } else {
      return true; // !A
    }
  }

}
