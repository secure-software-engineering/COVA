package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class ImpreciseSingle7 {

  public void test() {
    String a = Configuration.fieldA;
    String b = "";
    String c = b;
    b = a.substring(0, 2);
    if (b.contains("a")) {
      System.out.println();// im(FA)
    }
    if (c.contains("a")) {
      System.out.println();// no constraint
    }
  }

}
