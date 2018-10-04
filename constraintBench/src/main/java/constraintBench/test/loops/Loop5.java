package constraintBench.test.loops;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Loop5 {

  public void test() {
    for (int i = 0; i < Configuration.featureD(); i++) {
      if (Configuration.featureA())// im(D)
      {
        System.out.println();// im(D) ^ A
      }
    }
    System.out.println();// !im(D)
  }

}
