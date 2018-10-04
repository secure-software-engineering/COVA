package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class ImpreciseSingle3 {

  public void test() {
    if (Configuration.fieldA != null) {
      if (Configuration.fieldA.startsWith("test")) { // FA != null
        System.out.println();// (FA != null) ^ im(FA)
      } else {
        return;// / (FA != null) ^ !im(FA)
      }
    }
    System.out.println(); // (FA = null) V im(FA)
  }
}
