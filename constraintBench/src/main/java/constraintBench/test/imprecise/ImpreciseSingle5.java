package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class ImpreciseSingle5 {

  public void test() {
    int d = Configuration.featureD();
    int negD = -d;
    if (negD > -1) {
      System.out.println();// !im(D);
    }
  }

}
