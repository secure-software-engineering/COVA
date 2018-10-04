package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseSingle2 {

  public void test() {
    int d = Configuration.featureD();
    int f = Configuration.featureF();
    f = f + 1;
    if (d == f) {
      System.out.println();// D = im(F)_0
    }
  }
}
