package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple11 {

  public void test() {
    String fa = Configuration.fieldA;
    boolean a = fa.startsWith("a");
    String fc = Configuration.fieldC;
    if (Configuration.fieldB.startsWith("b")) {
      a = fc.equals("c");// im(FB)_0
    }
    if (a) {
      System.out.println();// (im(FB)_0 ^ im(FC)_0) v (!im(FB)_0 ^ im(FA)_0)
    }
  }
}
