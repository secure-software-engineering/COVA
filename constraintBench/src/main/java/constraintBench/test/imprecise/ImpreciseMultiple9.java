package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple9 {

  public void test() {
    String fa = Configuration.fieldA;
    String fb = Configuration.fieldB;
    if (fa.equals(fb)) {
      System.out.println();// im(FA+FB)_0
    }
  }

}
