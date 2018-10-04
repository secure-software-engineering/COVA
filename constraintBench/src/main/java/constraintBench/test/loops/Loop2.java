package constraintBench.test.loops;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Loop2 {

  public void test() {
    boolean a = Configuration.featureA();
    while (a) {
      System.out.println();// A
    }
    System.out.println();// !A
  }

}
