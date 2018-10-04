package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple4 {

  public void test() {
    int d = Configuration.featureD();
    if (d > 8) {
      int a = Configuration.fieldA.length();// D > 8
      int b = Configuration.fieldB.length();// D > 8
      int c = a + b;// D > 8
      if (c > 0) {// D > 8
        System.out.println(); // D>8 ^ !im(FA+FB)_0
      }
    }
  }
}
