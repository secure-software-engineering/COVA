package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 * 
 */
public class ImpreciseMultiple5 {

  public void test() {
    int d = Configuration.featureD();
    if (d > 8) {
      boolean b = Configuration.fieldA.startsWith("http:");// D > 8
      if (b) {// D > 8
        System.out.println();// D > 8 ^ str.prefixof("http:", FA)
      }
    }
  }

}
