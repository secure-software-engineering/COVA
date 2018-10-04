package constraintBench.test.loops;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Loop4 {

  public void test() {
    for (int i = 0; i < Configuration.featureD(); i++) {
      System.out.println();// !im(D)
    }
    System.out.println();// im(D)
  }

}
