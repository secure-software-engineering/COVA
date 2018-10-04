package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseSingle4 {

  public void test() {
    int d = Configuration.featureD();
    int r = callee();
    if (d > r) {
      System.out.println();// !im(D);
    }
  }

  public int callee() {
    int f = 0;
    f = f * 5 + 10;
    return f;
  }

}
