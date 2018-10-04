package constraintBench.test.infeasible;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Infeasible2 {

  public void test() {
    boolean a = Configuration.featureA();
    if (a) {
      callee();// A
    }
  }

  public void callee() {
    boolean a = Configuration.featureA();// A
    if (!a) {// A
      System.out.println();// false
    }
  }

}
