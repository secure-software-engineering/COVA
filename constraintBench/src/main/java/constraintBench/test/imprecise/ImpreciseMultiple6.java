package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple6 {

  public void test() {
    int d = Configuration.featureD();
    if (d > 8) {
      boolean b = Configuration.fieldA.startsWith("http:") || Configuration.fieldB.startsWith("http:");// D > 8
      if (b) {// D > 8
        System.out.println(); // (D > 8) ^ (im(FA)_0 v im(FB)_0)
      }
    }
  }
}
