package constraintBench.test.loops;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class Loop3 {

  public void test() {
    boolean a = Configuration.featureA();
    for (int i = 0; i < 100; i++) {
      if (a)
      {
        System.out.println();// A
      }
      System.out.println();
    }
    System.out.println();
  }

}
