package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 *
 */
public class ImpreciseSingle3 {

  public void test() {
    if (Configuration.fieldA != null) {
      if (Configuration.fieldA.startsWith("test")) { // FA != null
        System.out.println();// (FA != null) ^ str.prefixof("test", FA)
      } else {
        return;// / (FA != null) ^ !str.prefixof("test", FA)
      }
    }
    System.out.println(); // (FA = null) V str.prefixof("test", FA)
  }
}
