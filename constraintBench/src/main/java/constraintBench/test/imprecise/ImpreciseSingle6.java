package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseSingle6 {

  public void test() {
    int d = Configuration.featureD();
    int negD = ++d;
    if (negD > 2) {
      System.out.println();// !im(D);
    }
  }

}
