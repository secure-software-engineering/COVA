package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class IntegerMultiple2 {

  public void test() {
    int d = Configuration.featureD();
    int f = Configuration.featureF();
    if (d < f) {
      System.out.println();// !(D >= F)
    }
  }

}
